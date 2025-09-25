package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.AccountDto
import com.example.decalxeandroid.data.dto.CreateAccountDto
import com.example.decalxeandroid.data.dto.UpdateAccountDto
import com.example.decalxeandroid.domain.model.Account

object AccountMapper {
    fun mapAccountDtoToAccount(dto: AccountDto): Account {
        return Account(
            id = dto.accountID,
            customerId = "", // Account doesn't have customerId in the DTO
            customerName = "", // Account doesn't have customerName in the DTO
            accountNumber = dto.username, // Use username as account number
            accountType = dto.roleName, // Use roleName as account type
            balance = 0.0, // Account doesn't have balance in the DTO
            status = if (dto.isActive) "Active" else "Inactive",
            openingDate = "", // Account doesn't have openingDate in the DTO
            lastTransactionDate = null, // Account doesn't have lastTransactionDate in the DTO
            notes = null, // Account doesn't have notes in the DTO
            createdAt = "", // Account doesn't have createdAt in the DTO
            updatedAt = "" // Account doesn't have updatedAt in the DTO
        )
    }
    
    fun mapAccountToCreateAccountDto(account: Account): CreateAccountDto {
        return CreateAccountDto(
            username = account.accountNumber,
            email = null, // Account doesn't have email in the domain model
            password = "", // Account doesn't have password in the domain model
            roleID = account.accountType // Use accountType as roleID
        )
    }
    
    fun mapAccountToUpdateAccountDto(account: Account): UpdateAccountDto {
        return UpdateAccountDto(
            username = account.accountNumber,
            email = null, // Account doesn't have email in the domain model
            isActive = account.status == "Active",
            roleID = account.accountType // Use accountType as roleID
        )
    }
}

