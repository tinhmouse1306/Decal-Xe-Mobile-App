package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

object VietnameseInputUtils {
    
    /**
     * Get appropriate keyboard options for Vietnamese input
     */
    fun getVietnameseKeyboardOptions(
        keyboardType: KeyboardType = KeyboardType.Text,
        capitalization: KeyboardCapitalization = KeyboardCapitalization.Words
    ) = KeyboardOptions(
        keyboardType = keyboardType,
        capitalization = capitalization,
        imeAction = ImeAction.Next
    )
    
    /**
     * Validate Vietnamese text input
     */
    fun isValidVietnameseText(text: String): Boolean {
        if (text.isBlank()) return false
        
        // Allow all printable characters, spaces, and Vietnamese characters
        // More permissive validation to allow all Unicode characters
        return text.isNotBlank() && text.length >= 1
    }
    
    /**
     * Validate Vietnamese phone number
     */
    fun isValidVietnamesePhone(phone: String): Boolean {
        if (phone.isBlank()) return false
        
        // Vietnamese phone number patterns
        val patterns = listOf(
            "^0[0-9]{9}$",           // 0xxxxxxxxx
            "^\\+84[0-9]{9}$",       // +84xxxxxxxxx
            "^84[0-9]{9}$"           // 84xxxxxxxxx
        )
        
        return patterns.any { phone.matches(it.toRegex()) }
    }
    
    /**
     * Validate Vietnamese email
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return true // Email is optional
        
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }
    
    /**
     * Clean Vietnamese text input
     */
    fun cleanVietnameseText(text: String): String {
        // Only trim and normalize spaces, don't filter characters
        return text.trim()
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
    }
    
    /**
     * Clean Vietnamese phone number
     */
    fun cleanVietnamesePhone(phone: String): String {
        return phone.trim()
            .replace(Regex("[^0-9+]"), "") // Keep only numbers and +
            .replace(Regex("^84"), "0") // Convert 84 to 0
            .replace(Regex("^\\+84"), "0") // Convert +84 to 0
    }
    
    /**
     * Format Vietnamese name (capitalize first letter of each word)
     */
    fun formatVietnameseName(name: String): String {
        return name.trim()
            .split("\\s+".toRegex())
            .joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.lowercase().replaceFirstChar { 
                        if (it.isLowerCase()) it.titlecase() else it.toString() 
                    }
                } else {
                    word
                }
            }
    }
}
