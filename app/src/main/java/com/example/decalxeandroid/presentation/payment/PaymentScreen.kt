package com.example.decalxeandroid.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.decalxeandroid.presentation.customers.CustomerModernTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
        orderId: String,
        viewModel: PaymentViewModel,
        onNavigateBack: () -> Unit,
        onPaymentSuccess: () -> Unit,
        modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Debug logs
    println("PaymentScreen: orderId = $orderId")
    println("PaymentScreen: isLoading = ${uiState.isLoading}")
    println("PaymentScreen: error = ${uiState.error}")
    println("PaymentScreen: paymentData = ${uiState.paymentData}")
    println("PaymentScreen: isProcessing = ${uiState.isProcessing}")

    // Load payment data when screen is first composed
    LaunchedEffect(orderId) {
        println("PaymentScreen: LaunchedEffect triggered for orderId: $orderId")
        viewModel.loadPaymentData(orderId)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        CustomerModernTopAppBar(
                title = "Thanh toán",
                subtitle = "Đơn hàng #${orderId.take(8)}...",
                onNavigateBack = onNavigateBack
        )

        // Content
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                                text = "Đang tải thông tin thanh toán...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            uiState.error != null -> {
                val error = uiState.error!!
                ErrorState(error = error, onRetry = { viewModel.loadPaymentData(orderId) })
            }
            uiState.paymentData != null -> {
                val paymentData = uiState.paymentData!!
                PaymentContent(
                        paymentData = paymentData,
                        onPaymentMethodSelected = viewModel::selectPaymentMethod,
                        onProcessPayment = viewModel::processPayment,
                        isProcessing = uiState.isProcessing
                )
            }
        }
    }

    // Handle payment success
    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            onPaymentSuccess()
        }
    }
}

@Composable
private fun PaymentContent(
        paymentData: PaymentData,
        onPaymentMethodSelected: (PaymentMethod) -> Unit,
        onProcessPayment: () -> Unit,
        isProcessing: Boolean
) {
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Summary Card
        OrderSummaryCard(paymentData = paymentData)

        // Payment Methods Card
        PaymentMethodsCard(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { method ->
                    selectedPaymentMethod = method
                    onPaymentMethodSelected(method)
                }
        )

        // Payment Details Card
        if (selectedPaymentMethod != null) {
            PaymentDetailsCard(
                    paymentMethod = selectedPaymentMethod!!,
                    totalAmount = paymentData.totalAmount
            )
        }

        // Process Payment Button
        Button(
                onClick = onProcessPayment,
                enabled = selectedPaymentMethod != null && !isProcessing,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đang xử lý...")
            } else {
                Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = "Thanh toán ${paymentData.totalAmount.toInt()} VNĐ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(paymentData: PaymentData) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "Tóm tắt đơn hàng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            Divider()

            // Customer Info
            InfoRow(label = "Khách hàng", value = paymentData.customerName)

            InfoRow(label = "Số điện thoại", value = paymentData.customerPhone)

            InfoRow(label = "Dịch vụ", value = paymentData.serviceName)

            InfoRow(label = "Mô tả", value = paymentData.description)

            Divider()

            // Amount Details
            InfoRow(label = "Tổng tiền dịch vụ", value = "${paymentData.totalAmount.toInt()} VNĐ")

            InfoRow(label = "Phí dịch vụ", value = "0 VNĐ", valueColor = Color(0xFF4CAF50))

            Divider()

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        text = "Tổng thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = "${paymentData.totalAmount.toInt()} VNĐ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodsCard(
        selectedMethod: PaymentMethod?,
        onMethodSelected: (PaymentMethod) -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "Phương thức thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            Divider()

            // Cash Payment
            PaymentMethodItem(
                    method = PaymentMethod.CASH,
                    title = "Thanh toán tiền mặt",
                    description = "Thanh toán khi nhận hàng",
                    icon = Icons.Default.Money,
                    isSelected = selectedMethod == PaymentMethod.CASH,
                    onClick = { onMethodSelected(PaymentMethod.CASH) }
            )

            // Bank Transfer
            PaymentMethodItem(
                    method = PaymentMethod.BANK_TRANSFER,
                    title = "Chuyển khoản ngân hàng",
                    description = "Chuyển khoản qua ngân hàng",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedMethod == PaymentMethod.BANK_TRANSFER,
                    onClick = { onMethodSelected(PaymentMethod.BANK_TRANSFER) }
            )

            // E-Wallet
            PaymentMethodItem(
                    method = PaymentMethod.E_WALLET,
                    title = "Ví điện tử",
                    description = "Momo, ZaloPay, VNPay...",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedMethod == PaymentMethod.E_WALLET,
                    onClick = { onMethodSelected(PaymentMethod.E_WALLET) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodItem(
        method: PaymentMethod,
        title: String,
        description: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                    ),
            onClick = onClick
    ) {
        Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(selected = isSelected, onClick = onClick)

            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailsCard(paymentMethod: PaymentMethod, totalAmount: Double) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "Chi tiết thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            Divider()

            when (paymentMethod) {
                PaymentMethod.CASH -> {
                    Text(
                            text = "Thanh toán tiền mặt khi nhận hàng",
                            style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                            text = "Số tiền: ${totalAmount.toInt()} VNĐ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                    )
                }
                PaymentMethod.BANK_TRANSFER -> {
                    Text(
                            text = "Thông tin chuyển khoản:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                    )
                    InfoRow(label = "Ngân hàng", value = "Vietcombank")
                    InfoRow(label = "Số tài khoản", value = "1234567890")
                    InfoRow(label = "Chủ tài khoản", value = "Công ty TNHH DecalXe")
                    InfoRow(
                            label = "Nội dung",
                            value = "Thanh toan don hang #${System.currentTimeMillis()}"
                    )
                    InfoRow(
                            label = "Số tiền",
                            value = "${totalAmount.toInt()} VNĐ",
                            valueColor = Color(0xFF4CAF50)
                    )
                }
                PaymentMethod.E_WALLET -> {
                    Text(
                            text = "Quét mã QR để thanh toán",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                    )
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .height(200.dp)
                                            .background(
                                                    Color.Gray.copy(alpha = 0.1f),
                                                    RoundedCornerShape(8.dp)
                                            ),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                    text = "QR Code sẽ được tạo khi thanh toán",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
        label: String,
        value: String,
        valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor)
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
            )
            Text(
                    text = "Có lỗi xảy ra",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
            )
            Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) { Text("Thử lại") }
        }
    }
}

// Data classes
data class PaymentData(
        val orderId: String,
        val customerName: String,
        val customerPhone: String,
        val serviceName: String,
        val description: String,
        val totalAmount: Double
)

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    E_WALLET
}
