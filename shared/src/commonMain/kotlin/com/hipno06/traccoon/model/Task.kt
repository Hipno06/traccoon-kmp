package com.hipno06.traccoon.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Task (
    val id: Int,
    val title: String,
    val description: String = "",
    val deadline: LocalDate? = null,
    val priority: Int = 3,
    val isCompleted: Boolean = false
)