package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import java.text.NumberFormat
import java.util.*

private fun mapStageToVietnamese(stageName: String): String {
        return when (stageName) {
                "Pending" -> "Đơn hàng mới"
                "Survey" -> "Khảo sát"
                "Designing" -> "Thiết kế"
                "ProductionAndInstallation" -> "Chốt và thi công"
                "Payment" -> "Thanh toán"
                "AcceptanceAndDelivery" -> "Nghiệm thu và nhận hàng"
                else -> stageName
        }
}

@Composable
fun OrderInfoCard(
        order: Order,
        currentStage: OrderStageHistory? = null,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        // Header
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = order.customerFullName ?: "Khách hàng",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                )
                                OrderStatusChip(status = order.orderStatus)
                        }

                        Divider()

                        // Order details
                        InfoRow(
                                icon = Icons.Default.DateRange,
                                label = "Ngày đặt",
                                value = order.orderDate
                        )

                        InfoRow(
                                icon = Icons.Default.AttachMoney,
                                label = "Tổng tiền",
                                value =
                                        NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                                                .format(order.totalAmount)
                        )

                        InfoRow(
                                icon = Icons.Default.Timeline,
                                label = "Giai đoạn hiện tại",
                                value =
                                        when {
                                                currentStage?.stageName != null ->
                                                        mapStageToVietnamese(currentStage.stageName)
                                                order.currentStage == "0" -> "Chưa xác định"
                                                order.orderStatus == "0" -> "Chưa xác định"
                                                order.orderStatus.isNotEmpty() &&
                                                        order.orderStatus != "0" ->
                                                        order.orderStatus
                                                else -> order.currentStage
                                        }
                        )

                        InfoRow(
                                icon = Icons.Default.PriorityHigh,
                                label = "Độ ưu tiên",
                                value = order.priority ?: "Chưa có thông tin"
                        )

                        if (order.description?.isNotEmpty() == true) {
                                InfoRow(
                                        icon = Icons.Default.Description,
                                        label = "Mô tả",
                                        value = order.description
                                )
                        }
                }
        }
}

@Composable
fun CustomerInfoCard(order: Order, modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Thông tin khách hàng",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Divider()

                        InfoRow(
                                icon = Icons.Default.Person,
                                label = "Họ tên",
                                value = order.customerFullName
                        )

                        InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Số điện thoại",
                                value = order.customerPhoneNumber ?: "Chưa có thông tin"
                        )

                        if (order.customerEmail?.isNotEmpty() == true) {
                                InfoRow(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        value = order.customerEmail
                                )
                        }

                        if (order.customerAddress?.isNotEmpty() == true) {
                                InfoRow(
                                        icon = Icons.Default.LocationOn,
                                        label = "Địa chỉ",
                                        value = order.customerAddress
                                )
                        }
                }
        }
}

@Composable
fun VehicleInfoCard(order: Order, modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Thông tin xe",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Divider()

                        InfoRow(
                                icon = Icons.Default.DirectionsCar,
                                label = "Dòng xe",
                                value = "${order.vehicleBrandName} ${order.vehicleModelName}"
                        )

                        InfoRow(
                                icon = Icons.Default.Fingerprint,
                                label = "Số khung",
                                value = order.chassisNumber ?: "Chưa có thông tin"
                        )

                        InfoRow(
                                icon = Icons.Default.Schedule,
                                label = "Thời gian dự kiến",
                                value = order.expectedArrivalTime ?: "Chưa có thông tin"
                        )

                        if (order.isCustomDecal) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Build,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                                text = "Decal tùy chỉnh",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.secondary
                                        )
                                }
                        }
                }
        }
}

@Composable
fun OrderDetailsList(
        orderDetails: List<OrderDetail>,
        modifier: Modifier = Modifier,
        onEditOrderDetail: (OrderDetail) -> Unit = {},
        onDeleteOrderDetail: (OrderDetail) -> Unit = {}
) {
        // Debug log để xem số lượng orderDetails
        android.util.Log.d("OrderDetailsList", "OrderDetails count: ${orderDetails.size}")
        orderDetails.forEachIndexed { index, detail ->
                android.util.Log.d("OrderDetailsList", "OrderDetail $index: ${detail.serviceName}")
        }
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Chi tiết dịch vụ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Divider()

                        if (orderDetails.isEmpty()) {
                                Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = "Chưa có dịch vụ nào",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else {
                                orderDetails.forEach { orderDetail ->
                                        OrderDetailItem(
                                                orderDetail = orderDetail,
                                                onEdit = { onEditOrderDetail(orderDetail) },
                                                onDelete = { onDeleteOrderDetail(orderDetail) }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun OrderDetailItem(orderDetail: OrderDetail, onEdit: () -> Unit = {}, onDelete: () -> Unit = {}) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
        ) {
                Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = orderDetail.serviceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                                onClick = onEdit,
                                                modifier = Modifier.size(24.dp)
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Sửa",
                                                        modifier = Modifier.size(16.dp)
                                                )
                                        }
                                        IconButton(
                                                onClick = onDelete,
                                                modifier = Modifier.size(24.dp)
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Xóa",
                                                        modifier = Modifier.size(16.dp)
                                                )
                                        }
                                }
                        }

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        text = "Số lượng: ${orderDetail.quantity}",
                                        style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                        text =
                                                "Đơn giá: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(orderDetail.unitPrice)}",
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }

                        Text(
                                text =
                                        "Thành tiền: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(orderDetail.totalPrice)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                        )

                        if (orderDetail.description?.isNotEmpty() == true) {
                                Text(
                                        text = orderDetail.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
        }
}

@Composable
fun OrderStageTimeline(stageHistory: List<OrderStageHistory>, modifier: Modifier = Modifier) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Lịch sử giai đoạn",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Divider()

                        if (stageHistory.isEmpty()) {
                                Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = "Chưa có lịch sử giai đoạn",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else {
                                stageHistory.forEachIndexed { index, stage ->
                                        StageHistoryItem(
                                                stage = stage,
                                                isFirst = index == 0,
                                                isLast = index == stageHistory.size - 1
                                        )
                                }
                        }
                }
        }
}

@Composable
fun StageHistoryItem(stage: OrderStageHistory, isFirst: Boolean = false, isLast: Boolean = false) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                // Timeline indicator
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        if (!isFirst) {
                                Box(
                                        modifier =
                                                Modifier.width(2.dp)
                                                        .height(20.dp)
                                                        .padding(vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Divider(
                                                modifier = Modifier.fillMaxHeight(),
                                                color = MaterialTheme.colorScheme.outline
                                        )
                                }
                        }

                        Icon(
                                imageVector =
                                        if (stage.endDate != null) Icons.Default.CheckCircle
                                        else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint =
                                        if (stage.endDate != null) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                        )

                        if (!isLast) {
                                Box(
                                        modifier =
                                                Modifier.width(2.dp)
                                                        .height(20.dp)
                                                        .padding(vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Divider(
                                                modifier = Modifier.fillMaxHeight(),
                                                color = MaterialTheme.colorScheme.outline
                                        )
                                }
                        }
                }

                // Stage content
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        Text(
                                text = stage.stageName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                        )

                        if (stage.stageDescription?.isNotEmpty() == true) {
                                Text(
                                        text = stage.stageDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                        text = "Bắt đầu: ${stage.startDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (stage.endDate != null) {
                                        Text(
                                                text = "Kết thúc: ${stage.endDate}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }

                        Text(
                                text = "Nhân viên: ${stage.assignedEmployeeFullName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (stage.notes?.isNotEmpty() == true) {
                                Text(
                                        text = "Ghi chú: ${stage.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
        }
}

@Composable
fun OrderStatusChip(status: String, modifier: Modifier = Modifier) {
        val (backgroundColor, textColor) =
                when (status.lowercase()) {
                        "new" ->
                                MaterialTheme.colorScheme.primary to
                                        MaterialTheme.colorScheme.onPrimary
                        "in_progress" ->
                                MaterialTheme.colorScheme.secondary to
                                        MaterialTheme.colorScheme.onSecondary
                        "completed" ->
                                MaterialTheme.colorScheme.tertiary to
                                        MaterialTheme.colorScheme.onTertiary
                        "cancelled" ->
                                MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
                        else ->
                                MaterialTheme.colorScheme.surfaceVariant to
                                        MaterialTheme.colorScheme.onSurfaceVariant
                }

        Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = backgroundColor) {
                Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                )
        }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
        Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                        Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                        )
                }
        }
}
