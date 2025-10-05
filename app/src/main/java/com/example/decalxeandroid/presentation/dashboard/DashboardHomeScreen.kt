package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHomeScreen(
        viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory()),
        onNavigateToOrders: () -> Unit = {},
        onNavigateToCustomers: () -> Unit = {},
        onNavigateToCreateOrder: () -> Unit = {},
        onNavigateToProfile: () -> Unit = {},
        onNavigateToServices: () -> Unit = {}
) {
        val uiState by viewModel.uiState.collectAsState()

        // Get current user for role-based features
        LaunchedEffect(Unit) {
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(
                        com.example.decalxeandroid.di.AppContainer.authRepository
                )
        }

        val currentUser by
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser
                        .collectAsState()

        LaunchedEffect(Unit) { viewModel.loadDashboardData() }

        when {
                uiState.isLoading -> {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(48.dp),
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                                text = "Đang tải dữ liệu...",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }
                uiState.error != null -> {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Error,
                                                contentDescription = "Error",
                                                modifier = Modifier.size(48.dp),
                                                tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                                text = "Có lỗi xảy ra",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                                text = uiState.error ?: "Unknown error",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                        )
                                        Button(onClick = { viewModel.loadDashboardData() }) {
                                                Text("Thử lại")
                                        }
                                }
                        }
                }
                else -> {
                        LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                // Header
                                item {
                                        Text(
                                                text = "Dashboard",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold
                                        )
                                }

                                // Statistics Cards with Modern Design
                                item {
                                        LazyVerticalGrid(
                                                columns = GridCells.Fixed(2),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.height(200.dp)
                                        ) {
                                                item {
                                                        ModernStatCard(
                                                                title = "Tổng đơn hàng",
                                                                value =
                                                                        uiState.totalOrders
                                                                                .toString(),
                                                                icon = Icons.Default.ShoppingCart,
                                                                gradientColors =
                                                                        listOf(
                                                                                Color(0xFF667eea),
                                                                                Color(0xFF764ba2)
                                                                        )
                                                        )
                                                }
                                                item {
                                                        ModernStatCard(
                                                                title = "Khách hàng",
                                                                value =
                                                                        uiState.totalCustomers
                                                                                .toString(),
                                                                icon = Icons.Default.People,
                                                                gradientColors =
                                                                        listOf(
                                                                                Color(0xFFf093fb),
                                                                                Color(0xFFf5576c)
                                                                        )
                                                        )
                                                }
                                                item {
                                                        ModernStatCard(
                                                                title = "Dịch vụ",
                                                                value =
                                                                        uiState.totalServices
                                                                                .toString(),
                                                                icon = Icons.Default.DesignServices,
                                                                gradientColors =
                                                                        listOf(
                                                                                Color(0xFF4facfe),
                                                                                Color(0xFF00f2fe)
                                                                        )
                                                        )
                                                }
                                                item {
                                                        ModernStatCard(
                                                                title = "Doanh thu",
                                                                value =
                                                                        "${uiState.totalRevenue.toInt()}₫",
                                                                icon = Icons.Default.AttachMoney,
                                                                gradientColors =
                                                                        listOf(
                                                                                Color(0xFF43e97b),
                                                                                Color(0xFF38f9d7)
                                                                        )
                                                        )
                                                }
                                        }
                                }

                                // Welcome message with gradient
                                item { ModernWelcomeCard() }

                                // Decal Services
                                item {
                                        Text(
                                                text = "Dịch vụ Decal hiện có",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                        )
                                }

                                items(uiState.decalServices) { service ->
                                        ModernDecalServiceCard(service = service)
                                }
                        }
                }
        }
}

@Composable
fun ModernStatCard(title: String, value: String, icon: ImageVector, gradientColors: List<Color>) {
        Card(
                modifier = Modifier.fillMaxWidth().height(90.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(brush = Brush.linearGradient(gradientColors))
                ) {
                        Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = value,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )

                                Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                )
                        }
                }
        }
}

@Composable
fun ModernWelcomeCard() {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                brush =
                                                        Brush.linearGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color(0xFF667eea),
                                                                                Color(0xFF764ba2),
                                                                                Color(0xFFf093fb)
                                                                        )
                                                        )
                                        )
                ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                        text = "🎉 Chào mừng đến với DecalXe!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text =
                                                "Khám phá các dịch vụ decal chất lượng cao và tạo nên phong cách độc đáo cho xe của bạn",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                )
                        }
                }
        }
}

@Composable
fun ModernDecalServiceCard(service: com.example.decalxeandroid.domain.model.DecalService) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
        ) {
                Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Modern icon with gradient background
                        Box(
                                modifier =
                                        Modifier.size(56.dp)
                                                .clip(CircleShape)
                                                .background(
                                                        brush =
                                                                Brush.linearGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        Color(
                                                                                                0xFF667eea
                                                                                        ),
                                                                                        Color(
                                                                                                0xFF764ba2
                                                                                        )
                                                                                )
                                                                )
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Default.DesignServices,
                                        contentDescription = "Service",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = service.serviceName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = service.description ?: "Không có mô tả",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow =
                                                androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text = "${service.price.toInt()}₫",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF667eea)
                                )
                        }

                        // Modern status badge
                        Surface(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFF43e97b)
                        ) {
                                Text(
                                        text = "Có sẵn",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                ),
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                )
                        }
                }
        }
}
