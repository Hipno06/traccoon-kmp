package com.hipno06.traccoon

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform