package com.hipno06.traccoon

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.model.Task
import org.jetbrains.compose.resources.painterResource

import traccoon.shared.generated.resources.Res
import traccoon.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    // Task list
    val myTasks = remember { mutableStateListOf<Task>() }
    val taskIdCounter = remember { mutableStateOf(1) }

    val task1 = Task(taskIdCounter.value, "Tarea 1", "Descripción")
    val task2 = Task(taskIdCounter.value++, "Tarea 2")
    val task3 = task1
    myTasks.addAll(listOf(task1, task2, task3))

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title
            Text(text = "🦝 Traccoon 🦝", style = MaterialTheme.typography.titleLargeEmphasized)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Tareas:", style = MaterialTheme.typography.titleMedium)
            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (myTasks.isEmpty()) {
                    Text(text = "No hay tareas pendientes")
                } else {
                    myTasks.forEachIndexed { index, task ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically // Centra las cosas verticalmente
                        ) {
                            Column(modifier = Modifier) {
                                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}