package com.example.decalxeandroid.domain.usecase.order

import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.UserRole

/** Validator để kiểm tra quyền chuyển trạng thái đơn hàng dựa trên role và workflow */
class OrderStatusValidator {

    /** Kiểm tra xem user có quyền chuyển từ trạng thái hiện tại sang trạng thái mới không */
    fun canChangeStatus(
            currentOrder: Order,
            newStatus: String,
            userRole: UserRole,
            currentStageHistory: List<OrderStageHistory>
    ): ValidationResult {
        // Lấy trạng thái hiện tại
        val currentStatus = currentOrder.orderStatus

        // Kiểm tra workflow sequence
        val workflowResult =
                validateWorkflowSequence(currentStatus, newStatus, currentOrder.isCustomDecal)
        if (!workflowResult.isValid) {
            return workflowResult
        }

        // Kiểm tra role permissions
        val roleResult =
                validateRolePermission(
                        currentStatus,
                        newStatus,
                        userRole,
                        currentOrder.isCustomDecal
                )
        if (!roleResult.isValid) {
            return roleResult
        }

        // Kiểm tra prerequisites (các bước trước đó đã hoàn thành chưa)
        val prerequisiteResult =
                validatePrerequisites(
                        newStatus,
                        currentStageHistory,
                        currentOrder.isCustomDecal,
                        userRole
                )
        if (!prerequisiteResult.isValid) {
            return prerequisiteResult
        }

        return ValidationResult.Success
    }

    /** Kiểm tra thứ tự workflow có đúng không */
    private fun validateWorkflowSequence(
            currentStatus: String,
            newStatus: String,
            isCustomDecal: Boolean
    ): ValidationResult {
        val workflow =
                if (isCustomDecal) {
                    CUSTOM_DECAL_WORKFLOW
                } else {
                    STANDARD_DECAL_WORKFLOW
                }

        val currentIndex = workflow.indexOf(currentStatus)
        val newIndex = workflow.indexOf(newStatus)

        if (currentIndex == -1) {
            return ValidationResult.Error("Trạng thái hiện tại không hợp lệ: $currentStatus")
        }

        if (newIndex == -1) {
            return ValidationResult.Error("Trạng thái mới không hợp lệ: $newStatus")
        }

        // Không cho phép chuyển ngược lại
        if (newIndex < currentIndex) {
            return ValidationResult.Error(
                    "Không thể chuyển ngược từ '$currentStatus' về '$newStatus'"
            )
        }

        // Cho phép chuyển sang cùng trạng thái (không có thay đổi)
        if (newIndex == currentIndex) {
            return ValidationResult.Success
        }

        // Không cho phép nhảy bước (trừ trường hợp đặc biệt cho SALES)
        if (newIndex > currentIndex + 1) {
            // Cho phép SALES bỏ qua bước "Thanh toán" (từ "Chốt và thi công" sang "Nghiệm thu và
            // nhận hàng")
            val isSalesSkippingPayment =
                    currentStatus == "Chốt và thi công" && newStatus == "Nghiệm thu và nhận hàng"
            if (!isSalesSkippingPayment) {
                return ValidationResult.Error(
                        "Không thể bỏ qua bước. Phải hoàn thành '$currentStatus' trước"
                )
            }
        }

        return ValidationResult.Success
    }

    /** Kiểm tra quyền của role */
    private fun validateRolePermission(
            currentStatus: String,
            newStatus: String,
            userRole: UserRole,
            isCustomDecal: Boolean
    ): ValidationResult {
        val allowedTransitions =
                when (userRole) {
                    UserRole.SALES -> {
                        if (isCustomDecal) {
                            mapOf(
                                    "Đơn hàng mới" to listOf("Khảo sát"),
                                    "Khảo sát" to listOf("Thiết kế"),
                                    "Thiết kế" to listOf("Chốt và thi công"),
                                    "Chốt và thi công" to listOf("Nghiệm thu và nhận hàng")
                            )
                        } else {
                            mapOf(
                                    "Đơn hàng mới" to listOf("Khảo sát"),
                                    "Khảo sát" to listOf("Chốt và thi công"),
                                    "Chốt và thi công" to listOf("Nghiệm thu và nhận hàng")
                            )
                        }
                    }
                    UserRole.TECHNICIAN -> {
                        mapOf(
                                "Thiết kế" to listOf("Chốt và thi công"),
                                "Khảo sát" to listOf("Chốt và thi công") // Cho decal có sẵn
                        )
                    }
                    UserRole.CUSTOMER -> {
                        // Customer không có quyền chuyển trạng thái đơn hàng
                        return ValidationResult.Error(
                                "Khách hàng không có quyền chuyển trạng thái đơn hàng"
                        )
                    }
                    UserRole.MANAGER -> {
                        // Manager có thể chuyển bất kỳ trạng thái nào
                        return ValidationResult.Success
                    }
                    UserRole.ADMIN -> {
                        // Admin có thể chuyển bất kỳ trạng thái nào
                        return ValidationResult.Success
                    }
                }

        val allowedNextStatuses = allowedTransitions[currentStatus] ?: emptyList()
        if (newStatus !in allowedNextStatuses) {
            return ValidationResult.Error(
                    "Role ${userRole.name} không có quyền chuyển từ '$currentStatus' sang '$newStatus'"
            )
        }

        return ValidationResult.Success
    }

    /** Kiểm tra các điều kiện tiên quyết */
    private fun validatePrerequisites(
            newStatus: String,
            stageHistory: List<OrderStageHistory>,
            isCustomDecal: Boolean,
            userRole: UserRole? = null
    ): ValidationResult {
        when (newStatus) {
            "Thiết kế" -> {
                if (!hasCompletedStage(stageHistory, "Survey")) {
                    return ValidationResult.Error(
                            "Phải hoàn thành 'Khảo sát' trước khi chuyển sang 'Thiết kế'"
                    )
                }
            }
            "Chốt và thi công" -> {
                if (isCustomDecal) {
                    if (!hasCompletedStage(stageHistory, "Designing")) {
                        return ValidationResult.Error(
                                "Phải hoàn thành 'Thiết kế' trước khi chuyển sang 'Chốt và thi công'"
                        )
                    }
                } else {
                    if (!hasCompletedStage(stageHistory, "Survey")) {
                        return ValidationResult.Error(
                                "Phải hoàn thành 'Khảo sát' trước khi chuyển sang 'Chốt và thi công'"
                        )
                    }
                }
            }
            "Thanh toán" -> {
                if (!hasCompletedStage(stageHistory, "ProductionAndInstallation")) {
                    return ValidationResult.Error(
                            "Phải hoàn thành 'Chốt và thi công' trước khi chuyển sang 'Thanh toán'"
                    )
                }
            }
            "Nghiệm thu và nhận hàng" -> {
                // SALES có thể bỏ qua bước "Thanh toán"
                if (userRole != UserRole.SALES) {
                    if (!hasCompletedStage(stageHistory, "Payment")) {
                        return ValidationResult.Error(
                                "Phải hoàn thành 'Thanh toán' trước khi chuyển sang 'Nghiệm thu và nhận hàng'"
                        )
                    }
                } else {
                    // SALES chỉ cần hoàn thành "Chốt và thi công"
                    if (!hasCompletedStage(stageHistory, "ProductionAndInstallation")) {
                        return ValidationResult.Error(
                                "Phải hoàn thành 'Chốt và thi công' trước khi chuyển sang 'Nghiệm thu và nhận hàng'"
                        )
                    }
                }
            }
        }

        return ValidationResult.Success
    }

    /** Kiểm tra xem stage đã hoàn thành chưa */
    private fun hasCompletedStage(
            stageHistory: List<OrderStageHistory>,
            stageName: String
    ): Boolean {
        return stageHistory.any { it.stageName == stageName }
    }

    /** Lấy danh sách trạng thái có thể chuyển tiếp từ trạng thái hiện tại */
    fun getAvailableNextStatuses(
            currentOrder: Order,
            userRole: UserRole,
            currentStageHistory: List<OrderStageHistory>
    ): List<String> {
        val currentStatus = currentOrder.orderStatus
        val isCustomDecal = currentOrder.isCustomDecal

        val allPossibleStatuses =
                if (isCustomDecal) {
                    CUSTOM_DECAL_WORKFLOW
                } else {
                    STANDARD_DECAL_WORKFLOW
                }

        val currentIndex = allPossibleStatuses.indexOf(currentStatus)
        if (currentIndex == -1) return emptyList()

        // Lấy tất cả các trạng thái có thể chuyển tiếp (không chỉ bước tiếp theo)
        val availableStatuses = mutableListOf<String>()

        // Thêm trạng thái hiện tại (cho phép chuyển sang cùng trạng thái)
        availableStatuses.add(currentStatus)

        // Thêm các trạng thái tiếp theo có thể chuyển
        for (i in currentIndex + 1 until allPossibleStatuses.size) {
            val nextStatus = allPossibleStatuses[i]
            val validationResult =
                    canChangeStatus(currentOrder, nextStatus, userRole, currentStageHistory)
            if (validationResult.isValid) {
                availableStatuses.add(nextStatus)
            }
        }

        return availableStatuses
    }

    companion object {
        // Workflow cho decal tùy chỉnh
        private val CUSTOM_DECAL_WORKFLOW =
                listOf(
                        "Đơn hàng mới",
                        "Khảo sát",
                        "Thiết kế",
                        "Chốt và thi công",
                        "Thanh toán",
                        "Nghiệm thu và nhận hàng"
                )

        // Workflow cho decal có sẵn
        private val STANDARD_DECAL_WORKFLOW =
                listOf(
                        "Đơn hàng mới",
                        "Khảo sát",
                        "Chốt và thi công",
                        "Thanh toán",
                        "Nghiệm thu và nhận hàng"
                )
    }
}

/** Kết quả validation */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Success
}
