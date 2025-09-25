package com.example.decalxeandroid.data.remote

object ApiConstants {
    const val BASE_URL = "https://decalxesequences-production.up.railway.app/"
    
    // Auth endpoints (mobile API without /api prefix)
    const val LOGIN_ENDPOINT = "Auth/login"
    const val REGISTER_ENDPOINT = "Auth/register"
    const val CHANGE_PASSWORD_ENDPOINT = "Auth/change-password"
    const val RESET_PASSWORD_ENDPOINT = "Auth/reset-password"
    const val REFRESH_TOKEN_ENDPOINT = "Auth/refresh-token"
    
    // Customer endpoints
    const val CUSTOMERS_ENDPOINT = "Customers"
    const val CUSTOMER_BY_ID_ENDPOINT = "Customers/{id}"
    const val CUSTOMER_SEARCH_ENDPOINT = "Customers/search"
    const val CUSTOMER_BY_PHONE_ENDPOINT = "Customers/by-phone/{phoneNumber}"
    const val CUSTOMER_BY_EMAIL_ENDPOINT = "Customers/by-email/{email}"
    
    // Order endpoints
    const val ORDERS_ENDPOINT = "Orders"
    const val ORDER_BY_ID_ENDPOINT = "Orders/{id}"
    const val ORDER_BY_CUSTOMER_ENDPOINT = "Orders/by-customer/{customerId}"
    const val ORDER_BY_STATUS_ENDPOINT = "Orders/by-status/{status}"
    const val ORDER_BY_DATE_RANGE_ENDPOINT = "Orders/by-date-range"
    const val ORDER_STATISTICS_ENDPOINT = "Orders/statistics"
    
    // Customer Vehicle endpoints
    const val CUSTOMER_VEHICLES_ENDPOINT = "CustomerVehicles"
    const val CUSTOMER_VEHICLE_BY_ID_ENDPOINT = "CustomerVehicles/{id}"
    const val CUSTOMER_VEHICLES_BY_CUSTOMER_ENDPOINT = "CustomerVehicles/by-customer/{customerId}"
    
    // Employee endpoints
    const val EMPLOYEES_ENDPOINT = "Employees"
    const val EMPLOYEE_BY_ID_ENDPOINT = "Employees/{id}"
    const val EMPLOYEE_BY_ROLE_ENDPOINT = "Employees/by-role/{role}"
    const val EMPLOYEE_STATISTICS_ENDPOINT = "Employees/statistics"
    
    // Service endpoints
    const val SERVICES_ENDPOINT = "Services"
    const val SERVICE_BY_ID_ENDPOINT = "Services/{id}"
    const val SERVICE_BY_CATEGORY_ENDPOINT = "Services/by-category/{category}"
    
    // Store endpoints
    const val STORES_ENDPOINT = "Stores"
    const val STORE_BY_ID_ENDPOINT = "Stores/{id}"
    const val STORE_BY_LOCATION_ENDPOINT = "Stores/by-location"
    
    // Vehicle endpoints
    const val VEHICLES_ENDPOINT = "Vehicles"
    const val VEHICLE_BY_ID_ENDPOINT = "Vehicles/{id}"
    const val VEHICLE_BY_BRAND_ENDPOINT = "Vehicles/by-brand/{brand}"
    const val VEHICLE_BY_MODEL_ENDPOINT = "Vehicles/by-model/{model}"
    
    // Payment endpoints
    const val PAYMENTS_ENDPOINT = "Payments"
    const val PAYMENT_BY_ID_ENDPOINT = "Payments/{id}"
    const val PAYMENT_BY_ORDER_ENDPOINT = "Payments/by-order/{orderId}"
    const val PAYMENT_STATISTICS_ENDPOINT = "Payments/statistics"
    
    // Account endpoints
    const val ACCOUNTS_ENDPOINT = "Accounts"
    const val ACCOUNT_BY_ID_ENDPOINT = "Accounts/{id}"
    const val ACCOUNT_BY_CUSTOMER_ENDPOINT = "Accounts/by-customer/{customerId}"
    const val ACCOUNT_STATISTICS_ENDPOINT = "Accounts/statistics"
    
    // Analytics endpoints
    const val ANALYTICS_DASHBOARD_ENDPOINT = "Analytics/dashboard"
    const val ANALYTICS_REVENUE_ENDPOINT = "Analytics/revenue"
    const val ANALYTICS_ORDERS_ENDPOINT = "Analytics/orders"
    const val ANALYTICS_CUSTOMERS_ENDPOINT = "Analytics/customers"
}



