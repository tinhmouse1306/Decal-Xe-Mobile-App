package com.example.decalxeandroid.di

import com.example.decalxeandroid.data.api.CustomerApi
import com.example.decalxeandroid.data.api.CustomerVehicleApi
import com.example.decalxeandroid.data.api.DecalServiceApi
import com.example.decalxeandroid.data.api.EmployeeApi
import com.example.decalxeandroid.data.remote.OrderApiService
import com.example.decalxeandroid.data.remote.CustomerApiService
import com.example.decalxeandroid.data.mapper.CustomerMapper
import com.example.decalxeandroid.data.mapper.CustomerVehicleMapper
import com.example.decalxeandroid.data.mapper.DecalServiceMapper
import com.example.decalxeandroid.data.mapper.EmployeeMapper
import com.example.decalxeandroid.data.mapper.OrderMapper
import com.example.decalxeandroid.data.mapper.OrderDetailMapper
import com.example.decalxeandroid.data.mapper.OrderStageHistoryMapper
import com.example.decalxeandroid.data.repository.CustomerRepositoryImpl
import com.example.decalxeandroid.data.repository.CustomerVehicleRepositoryImpl
import com.example.decalxeandroid.data.repository.DecalServiceRepositoryImpl
import com.example.decalxeandroid.data.repository.EmployeeRepositoryImpl
import com.example.decalxeandroid.data.repository.OrderRepositoryImpl
import com.example.decalxeandroid.data.repository.AuthRepositoryImpl
import com.example.decalxeandroid.data.repository.VehicleRepositoryImpl
import com.example.decalxeandroid.data.remote.AuthApiService
import com.example.decalxeandroid.data.remote.VehicleApiService
import com.example.decalxeandroid.data.local.TokenManager
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.repository.AuthRepository
import com.example.decalxeandroid.domain.repository.VehicleRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object AppContainer {
    
    // Base URL for API
    private const val BASE_URL = "https://decalxesequences-production.up.railway.app/api/"
    
    // HTTP Logging Interceptor
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    // OkHttp Client
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", "DecalXeAndroid/1.0")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(requestWithUserAgent)
            }
            .build()
    }
    
    // Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Services
    private val customerApi: CustomerApi by lazy {
        retrofit.create(CustomerApi::class.java)
    }
    
    private val customerVehicleApi: CustomerVehicleApi by lazy {
        retrofit.create(CustomerVehicleApi::class.java)
    }
    
    private val employeeApi: EmployeeApi by lazy {
        retrofit.create(EmployeeApi::class.java)
    }
    
    private val orderApiService: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }
    
    private val decalServiceApi: DecalServiceApi by lazy {
        retrofit.create(DecalServiceApi::class.java)
    }
    
    private val customerApiService: CustomerApiService by lazy {
        retrofit.create(CustomerApiService::class.java)
    }
    
    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    private val vehicleApiService: VehicleApiService by lazy {
        retrofit.create(VehicleApiService::class.java)
    }
    
    private val tokenManager: TokenManager by lazy {
        TokenManager()
    }
    
    // Mappers
    private val customerMapper: CustomerMapper by lazy {
        CustomerMapper()
    }
    
    private val customerVehicleMapper: CustomerVehicleMapper by lazy {
        CustomerVehicleMapper()
    }
    
    private val employeeMapper: EmployeeMapper by lazy {
        EmployeeMapper()
    }
    
    private val orderMapper: OrderMapper by lazy {
        OrderMapper()
    }
    
    private val orderDetailMapper: OrderDetailMapper by lazy {
        OrderDetailMapper()
    }
    
    private val orderStageHistoryMapper: OrderStageHistoryMapper by lazy {
        OrderStageHistoryMapper()
    }
    
    private val decalServiceMapper: DecalServiceMapper by lazy {
        DecalServiceMapper()
    }
    
    // Repositories
    val customerRepository: CustomerRepository by lazy {
        CustomerRepositoryImpl(customerApi, customerMapper)
    }
    
    val customerVehicleRepository: CustomerVehicleRepository by lazy {
        CustomerVehicleRepositoryImpl(customerVehicleApi, customerVehicleMapper)
    }
    
    val employeeRepository: EmployeeRepository by lazy {
        EmployeeRepositoryImpl(employeeApi, employeeMapper)
    }
    
    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(orderApiService, orderMapper, orderDetailMapper, orderStageHistoryMapper)
    }
    
    val decalServiceRepository: DecalServiceRepository by lazy {
        DecalServiceRepositoryImpl(decalServiceApi, decalServiceMapper)
    }
    
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, customerApiService, tokenManager)
    }
    
    val vehicleRepository: VehicleRepository by lazy {
        VehicleRepositoryImpl(vehicleApiService)
    }
}
