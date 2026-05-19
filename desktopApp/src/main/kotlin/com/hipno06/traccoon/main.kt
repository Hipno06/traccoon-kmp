package com.hipno06.traccoon

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Traccoon",
    ) {
        App()
    }
}