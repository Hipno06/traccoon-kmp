package com.hipno06.traccoon.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

fun generateTaskHash(): String {
    // Base62
    val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..8).map { allowedChars.random() }.joinToString( "" )
}
@Serializable
data class Task (
    val id: String = generateTaskHash(),
    val title: String,
    val description: String = "",
    val deadline: LocalDate? = null,
    val priority: Int = 3,
    val isCompleted: Boolean = false
)