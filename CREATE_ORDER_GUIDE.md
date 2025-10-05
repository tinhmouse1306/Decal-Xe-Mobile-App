# 📋 Hướng dẫn tạo đơn hàng - DecalXe Android

## 🎯 **Logic đúng của việc tạo đơn hàng:**

### **Bước 1: Thông tin cơ bản**
- ✅ **Chọn khách hàng** (Customer)
- ✅ **Chọn xe** (Vehicle) 
- ✅ **Chọn nhân viên phụ trách** (Technician cùng store)
- ✅ **Độ ưu tiên** (Priority)
- ✅ **Mô tả** (Description)
- ✅ **Thời gian dự kiến** (Expected Arrival Time)

### **Bước 2: Thêm dịch vụ (OrderDetails)**
- ✅ **Chọn dịch vụ** từ danh sách DecalService
- ✅ **Nhập số lượng** cho mỗi dịch vụ
- ✅ **Tự động tính giá** dựa trên dịch vụ + số lượng
- ✅ **Tổng tiền tự động** = Sum(OrderDetails.totalPrice)

### **Bước 3: Xác nhận và tạo**
- ✅ **Review thông tin** đơn hàng
- ✅ **Tạo Order** trước
- ✅ **Tạo OrderDetails** sau (liên kết với Order vừa tạo)

---

## 🔧 **Cần sửa trong code:**

### **1. Thêm DecalService selection**
```kotlin
// Trong CreateOrderViewModel
val decalServices: List<DecalService> = emptyList()

// Trong OrderFormData
val selectedServices: List<OrderDetailFormData> = emptyList()
```

### **2. Tính tổng tiền tự động**
```kotlin
val totalAmount: Double
    get() = selectedServices.sumOf { it.totalPrice }
```

### **3. Flow tạo đơn hàng**
```kotlin
// 1. Tạo Order trước
val order = Order(...)
val createdOrder = orderRepository.createOrder(order)

// 2. Tạo OrderDetails sau
selectedServices.forEach { service ->
    val orderDetail = OrderDetail(
        orderId = createdOrder.orderId,
        serviceId = service.serviceId,
        quantity = service.quantity,
        unitPrice = service.unitPrice,
        totalPrice = service.totalPrice
    )
    orderDetailRepository.createOrderDetail(orderDetail)
}
```

---

## 📱 **UI Flow mới:**

### **Tab 1: Thông tin cơ bản**
- Customer selection
- Vehicle selection  
- Employee selection
- Priority, Description, Expected time

### **Tab 2: Dịch vụ**
- Danh sách DecalService
- Thêm/xóa dịch vụ
- Nhập số lượng
- Xem tổng tiền tự động

### **Tab 3: Xác nhận**
- Review tất cả thông tin
- Nút "Tạo đơn hàng"

---

## 🎨 **Components cần tạo:**

1. **ServiceSelectionField** - Chọn dịch vụ
2. **ServiceQuantityField** - Nhập số lượng
3. **ServiceListCard** - Hiển thị danh sách dịch vụ đã chọn
4. **TotalAmountCard** - Hiển thị tổng tiền tự động
5. **OrderConfirmationScreen** - Màn hình xác nhận

---

## 📊 **Data Models cần thêm:**

```kotlin
data class OrderDetailFormData(
    val serviceId: String,
    val serviceName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

data class CreateOrderFormData(
    // Basic info
    val selectedCustomerId: String?,
    val selectedVehicleId: String?,
    val selectedEmployeeId: String?,
    val priority: String,
    val description: String,
    val expectedArrivalTime: String,
    
    // Services
    val selectedServices: List<OrderDetailFormData>,
    
    // Auto calculated
    val totalAmount: Double
)
```

---

## ✅ **Kết quả mong đợi:**

1. **Logic đúng**: Order → OrderDetails
2. **Tổng tiền chính xác**: Tính từ dịch vụ thực tế
3. **UX tốt**: Flow rõ ràng, dễ hiểu
4. **Data integrity**: Đảm bảo tính nhất quán dữ liệu

---

## 🚀 **Next Steps:**

1. Sửa CreateOrderViewModel để load DecalServices
2. Thêm UI components cho service selection
3. Implement auto-calculation cho total amount
4. Sửa flow tạo đơn hàng: Order → OrderDetails
5. Test toàn bộ flow tạo đơn hàng
