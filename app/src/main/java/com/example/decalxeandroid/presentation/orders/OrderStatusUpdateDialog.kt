package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun OrderStatusUpdateDialog(
        currentStatus: String,
        currentStage: String,
        isCustomDecal: Boolean,
        availableStatuses: List<String> = emptyList(),
        onDismiss: () -> Unit,
        onStatusUpdate: (status: String, stage: String) -> Unit
) {
        var selectedStage by remember { mutableStateOf(currentStage) }

        // Mapping từ stage sang status theo workflow
        val stageToStatusMap =
                if (isCustomDecal) {
                        // isCustomDecal = true: Có bước thiết kế
                        mapOf(
                                "Pending" to "Đơn hàng mới",
                                "Survey" to "Khảo sát",
                                "Designing" to "Thiết kế",
                                "ProductionAndInstallation" to "Chốt và thi công",
                                "Payment" to "Thanh toán",
                                "AcceptanceAndDelivery" to "Nghiệm thu và nhận hàng"
                        )
                } else {
                        // isCustomDecal = false: Bỏ qua bước thiết kế
                        mapOf(
                                "Pending" to "Đơn hàng mới",
                                "Survey" to "Khảo sát",
                                "ProductionAndInstallation" to "Chốt và thi công",
                                "Payment" to "Thanh toán",
                                "AcceptanceAndDelivery" to "Nghiệm thu và nhận hàng"
                        )
                }

        val selectedStatus = stageToStatusMap[selectedStage] ?: currentStatus

        Dialog(onDismissRequest = onDismiss) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                        Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                // Header
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "Cập nhật trạng thái đơn hàng",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                        )
                                        IconButton(onClick = onDismiss) {
                                                Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Đóng"
                                                )
                                        }
                                }

                                Divider()

                                // Status Display (Read-only, auto-updated based on stage)
                                Text(
                                        text = "Trạng thái đơn hàng (tự động cập nhật)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                )

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                )
                                ) {
                                        Text(
                                                text = selectedStatus,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(16.dp)
                                        )
                                }

                                // Stage Selection
                                Text(
                                        text = "Giai đoạn đơn hàng",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                )

                                // Stage options theo isCustomDecal và availableStatuses
                                val allStageOptions =
                                        if (isCustomDecal) {
                                                // isCustomDecal = true: Có bước thiết kế
                                                listOf(
                                                        "Pending" to "Đơn hàng mới",
                                                        "Survey" to "Khảo sát",
                                                        "Designing" to "Thiết kế",
                                                        "ProductionAndInstallation" to
                                                                "Chốt và thi công",
                                                        "Payment" to "Thanh toán",
                                                        "AcceptanceAndDelivery" to
                                                                "Nghiệm thu và nhận hàng"
                                                )
                                        } else {
                                                // isCustomDecal = false: Bỏ qua bước thiết kế
                                                listOf(
                                                        "Pending" to "Đơn hàng mới",
                                                        "Survey" to "Khảo sát",
                                                        "ProductionAndInstallation" to
                                                                "Chốt và thi công",
                                                        "Payment" to "Thanh toán",
                                                        "AcceptanceAndDelivery" to
                                                                "Nghiệm thu và nhận hàng"
                                                )
                                        }

                                // Lọc chỉ những stage có status tương ứng trong availableStatuses
                                val stageOptions =
                                        if (availableStatuses.isNotEmpty()) {
                                                allStageOptions.filter { (_, status) ->
                                                        status in availableStatuses
                                                }
                                        } else {
                                                allStageOptions
                                        }

                                LazyColumn(
                                        modifier = Modifier.height(200.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                        items(stageOptions) { (stage, displayName) ->
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(
                                                                                vertical = 8.dp,
                                                                                horizontal = 4.dp
                                                                        ),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        RadioButton(
                                                                selected = selectedStage == stage,
                                                                onClick = { selectedStage = stage }
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                text = displayName,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium
                                                        )
                                                }
                                        }
                                }

                                // Action Buttons
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        OutlinedButton(
                                                onClick = onDismiss,
                                                modifier = Modifier.weight(1f)
                                        ) { Text("Hủy") }

                                        Button(
                                                onClick = {
                                                        onStatusUpdate(
                                                                selectedStatus,
                                                                selectedStage
                                                        )
                                                        onDismiss()
                                                },
                                                modifier = Modifier.weight(1f)
                                        ) { Text("Cập nhật") }
                                }
                        }
                }
        }
}
