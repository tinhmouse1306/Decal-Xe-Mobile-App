package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.model.*

// import com.example.decalxeandroid.presentation.sales.ServiceSelectionItem

@Composable
fun OrderDetailForm(
        createdOrder: Order,
        formData: OrderFormData,
        customers: List<Customer>,
        vehicles: List<CustomerVehicle>,
        employees: List<Employee>,
        decalServices: List<DecalService>,
        onServiceAdd: (DecalService, Int) -> Unit,
        onServiceRemove: (String) -> Unit,
        onServiceQuantityChange: (String, Int) -> Unit,
        onCompleteOrder: () -> Unit,
        paddingValues: PaddingValues
) {
    LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Order Information Card
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                            text = "Thông tin đơn hàng đã tạo",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )

                    Text("Mã đơn hàng: ${createdOrder.orderId}")
                    Text("Khách hàng: ${createdOrder.customerFullName}")
                    Text("Trạng thái: ${createdOrder.orderStatus}")
                    Text("Tổng tiền: ${String.format("%.0f", createdOrder.totalAmount)} VNĐ")
                }
            }
        }

        item {
            Text(
                    text = "Bước 2: Thêm chi tiết dịch vụ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                    text = "Chọn dịch vụ cần thực hiện cho đơn hàng này:",
                    style = MaterialTheme.typography.bodyLarge
            )
        }

        // Service Selection
        item {
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "Chọn dịch vụ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Use Column instead of LazyColumn to avoid nested scrollable issue
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        decalServices.forEach { service ->
                            val isSelected =
                                    formData.selectedServices.any {
                                        it.serviceId == service.serviceId
                                    }
                            Card(
                                    modifier =
                                            Modifier.fillMaxWidth().clickable {
                                                onServiceAdd(service, 1)
                                            },
                                    colors =
                                            CardDefaults.cardColors(
                                                    containerColor =
                                                            if (isSelected)
                                                                    MaterialTheme.colorScheme
                                                                            .primaryContainer
                                                            else MaterialTheme.colorScheme.surface
                                            )
                            ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                                text = service.serviceName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                                text =
                                                        "Giá: ${service.price?.let { String.format("%,.0f", it) } ?: "N/A"} VNĐ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Đã chọn",
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Services
        if (formData.selectedServices.isNotEmpty()) {
            item {
                Text(
                        text = "Dịch vụ đã chọn:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }

            items(formData.selectedServices) { service ->
                OrderDetailItem(
                        orderDetail =
                                OrderDetail(
                                        orderDetailId = "",
                                        orderId = createdOrder.orderId,
                                        serviceId = service.serviceId,
                                        serviceName = service.serviceName,
                                        quantity = service.quantity,
                                        unitPrice = service.unitPrice,
                                        totalPrice = service.totalPrice,
                                        description = null
                                ),
                        onEdit = {},
                        onDelete = { onServiceRemove(service.serviceId) }
                )
            }

            item {
                // Total Amount
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "Tổng tiền:",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                text = "${String.format("%.0f", formData.totalAmount)} VNĐ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                // Complete Order Button
                Button(
                        onClick = onCompleteOrder,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                )
                ) {
                    Text(
                            text = "Hoàn thành tạo đơn hàng",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            item {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                ) {
                    Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                                text = "Chưa có dịch vụ nào được chọn",
                                style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                                text = "Vui lòng chọn ít nhất một dịch vụ để tiếp tục",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
