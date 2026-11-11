package com.example.data.model

import java.time.LocalDate

/**
 * Numerology profile for the user and family members.
 * Saved locally on device.
 */
data class NumerologyProfile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String, // "Me", "Spouse", "Son", "Daughter", "Father", "Mother", or Custom
    val fullName: String, // In English spelling as per documents
    val birthDate: LocalDate,
    val gender: String = "Unspecified", // "Male", "Female", "Other", "Unspecified"
    val createdAt: Long = System.currentTimeMillis()
)
