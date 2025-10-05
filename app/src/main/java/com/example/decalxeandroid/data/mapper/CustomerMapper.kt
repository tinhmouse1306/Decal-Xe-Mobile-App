package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.CreateCustomerDto
import com.example.decalxeandroid.data.dto.CustomerDto
import com.example.decalxeandroid.data.dto.UpdateCustomerDto
import com.example.decalxeandroid.domain.model.Customer

class CustomerMapper {

    fun toDomain(dto: CustomerDto): Customer {
        return Customer(
                customerId = dto.customerID,
                fullName = dto.customerFullName.ifEmpty { "${dto.firstName} ${dto.lastName}" },
                email = dto.email,
                phoneNumber = dto.phoneNumber,
                address = dto.address,
                dateOfBirth = null, // CustomerDto doesn't have dateOfBirth
                gender = null, // CustomerDto doesn't have gender
                isActive = true, // CustomerDto doesn't have isActive
                createdAt = "", // CustomerDto doesn't have createdAt
                updatedAt = null // CustomerDto doesn't have updatedAt
        )
    }

    fun toDto(customer: Customer): CustomerDto {
        val nameParts = customer.fullName.split(" ", limit = 2)
        return CustomerDto(
                customerID = customer.customerId,
                firstName = nameParts.getOrNull(0) ?: "",
                lastName = nameParts.getOrNull(1) ?: "",
                customerFullName = customer.fullName,
                phoneNumber = customer.phoneNumber ?: "",
                email = customer.email,
                address = customer.address,
                accountID = null, // Customer doesn't have accountID in domain model
                accountUsername = null, // Customer doesn't have accountUsername in domain model
                accountRoleName = null // Customer doesn't have accountRoleName in domain model
        )
    }

    fun toCreateDto(customer: Customer): CreateCustomerDto {
        val nameParts = customer.fullName.split(" ", limit = 2)
        return CreateCustomerDto(
                firstName = nameParts.getOrNull(0) ?: "",
                lastName = nameParts.getOrNull(1) ?: "",
                phoneNumber = customer.phoneNumber ?: "",
                email = customer.email,
                address = customer.address,
                accountID = null // Customer doesn't have accountID in domain model
        )
    }

    fun toUpdateDto(customer: Customer): UpdateCustomerDto {
        val nameParts = customer.fullName.split(" ", limit = 2)
        return UpdateCustomerDto(
                firstName = nameParts.getOrNull(0) ?: "",
                lastName = nameParts.getOrNull(1) ?: "",
                phoneNumber = customer.phoneNumber ?: "",
                email = customer.email,
                address = customer.address
        )
    }
}
