package com.hipno06.traccoon.model

import kotlinx.serialization.Serializable

@Serializable
data class Task (
    val id: Int,
    val title: String,
    val description: String = "",
    val deadline: String = "",
    val priority: Int = 3,
    val isCompleted: Boolean = false
)