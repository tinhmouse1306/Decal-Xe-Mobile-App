package com.example.decalxeandroid.presentation.technician

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.usecase.order.OrderStatusValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallationStatusUpdateDialog(
        currentStatus: String,
        currentStage: String,
        isCustomDecal: Boolean,
        onDismiss: () -> Unit,
        onStatusUpdate: (String, String) -> Unit
) {
        val validator = remember { OrderStatusValidator() }
        val currentUser = remember {
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser.value
        }

        // For technician, we only allow updating from "Khảo sát" to "Chốt và thi công"
        val availableStatuses =
                remember(currentStatus, isCustomDecal) {
                        if (currentStatus == "Khảo sát") {
                                listOf("Chốt và thi công")
                        } else {
                                emptyList()
                        }
                }

        var selectedStatus by remember { mutableStateOf(availableStatuses.firstOrNull() ?: "") }
        var showValidationError by remember { mutableStateOf(false) }
        var validationError by remember { mutableStateOf("") }

        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text(
                                text = "Cập nhật trạng thái đơn hàng",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                        )
                },
                text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Current status info
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                )
                                ) {
                                        Column(
                                                modifier = Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                Text(
                                                        text = "Trạng thái hiện tại",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                        text = currentStatus,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                        text = "Giai đoạn: $currentStage",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        }
                                }

                                // Available statuses
                                if (availableStatuses.isNotEmpty()) {
                                        Text(
                                                text = "Chọn trạng thái mới:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                        )

                                        LazyColumn(
                                                modifier = Modifier.heightIn(max = 200.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                items(availableStatuses) { status ->
                                                        val isSelected = selectedStatus == status
                                                        Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                onClick = {
                                                                        selectedStatus = status
                                                                },
                                                                colors =
                                                                        CardDefaults.cardColors(
                                                                                containerColor =
                                                                                        if (isSelected
                                                                                        ) {
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primaryContainer
                                                                                        } else {
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .surface
                                                                                        }
                                                                        ),
                                                                border =
                                                                        if (isSelected) {
                                                                                androidx.compose
                                                                                        .foundation
                                                                                        .BorderStroke(
                                                                                                2.dp,
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                        )
                                                                        } else null
                                                        ) {
                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .padding(
                                                                                                16.dp
                                                                                        ),
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically,
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .SpaceBetween
                                                                ) {
                                                                        Column {
                                                                                Text(
                                                                                        text =
                                                                                                status,
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodyLarge,
                                                                                        fontWeight =
                                                                                                if (isSelected
                                                                                                )
                                                                                                        FontWeight
                                                                                                                .Bold
                                                                                                else
                                                                                                        FontWeight
                                                                                                                .Normal
                                                                                )
                                                                                Text(
                                                                                        text =
                                                                                                getStatusDescription(
                                                                                                        status
                                                                                                ),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodySmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                        if (isSelected) {
                                                                                Icon(
                                                                                        imageVector =
                                                                                                Icons.Default
                                                                                                        .CheckCircle,
                                                                                        contentDescription =
                                                                                                "Đã chọn",
                                                                                        tint =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                } else {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .errorContainer
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Warning,
                                                                contentDescription = null,
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onErrorContainer
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                text =
                                                                        "Không có trạng thái nào có thể chuyển từ '$currentStatus'",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onErrorContainer
                                                        )
                                                }
                                        }
                                }

                                // Validation error
                                if (showValidationError) {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .errorContainer
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Error,
                                                                contentDescription = null,
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onErrorContainer
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                text = validationError,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onErrorContainer
                                                        )
                                                }
                                        }
                                }
                        }
                },
                confirmButton = {
                        Button(
                                onClick = {
                                        if (selectedStatus.isNotEmpty()) {
                                                // Map status to stage
                                                val newStage =
                                                        mapStatusToStage(
                                                                selectedStatus,
                                                                isCustomDecal
                                                        )
                                                onStatusUpdate(selectedStatus, newStage)
                                        }
                                },
                                enabled =
                                        availableStatuses.isNotEmpty() &&
                                                selectedStatus.isNotEmpty()
                        ) { Text("Cập nhật") }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
        )
}

private fun getStatusDescription(status: String): String {
        return when (status) {
                "Đơn hàng mới" -> "Đơn hàng vừa được tạo"
                "Khảo sát" -> "Đang khảo sát địa điểm và yêu cầu"
                "Thiết kế" -> "Đang thiết kế decal theo yêu cầu"
                "Chốt và thi công" -> "Bắt đầu sản xuất và lắp đặt"
                "Thanh toán" -> "Chờ thanh toán từ khách hàng"
                "Nghiệm thu và nhận hàng" -> "Hoàn thành và giao hàng"
                else -> "Trạng thái: $status"
        }
}

private fun mapStatusToStage(status: String, isCustomDecal: Boolean): String {
        return when (status) {
                "Đơn hàng mới" -> "Pending"
                "Khảo sát" -> "Survey"
                "Thiết kế" -> "Designing"
                "Chốt và thi công" -> "ProductionAndInstallation"
                "Thanh toán" -> "Payment"
                "Nghiệm thu và nhận hàng" -> "AcceptanceAndDelivery"
                else -> status
        }
}
