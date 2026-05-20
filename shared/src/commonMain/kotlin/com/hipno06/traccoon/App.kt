package com.hipno06.traccoon

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.model.Task

// import traccoon.shared.generated.resources.Res
// import traccoon.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    // Task list
    val myTasks = remember { mutableStateListOf<Task>() }
    var taskIdCounter by remember { mutableStateOf(1) }

    // States to save what user writes inside text boxes
    var inputTitle by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }
    /*
    val task1 = Task(taskIdCounter.value, "Tarea 1", "Descripción")
    val task2 = Task(taskIdCounter.value++, "Tarea 2")
    val task3 = task1
    myTasks.addAll(listOf(task1, task2, task3))
*/
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

            // Text Box: Title
            TraccoonTextField(
                value = inputTitle,
                onValueChange = { inputTitle = it },
                label = "Título de la tarea"
            )
            // Text Box: Description
            TraccoonTextField(
                value = inputDescription,
                onValueChange = { inputDescription = it },
                label = "Descripción (opcional)"
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Button: save task
            Button(
                onClick = {
                    // Only save if title isn't empty
                    if (inputTitle.isNotBlank()) {
                        val newTask = Task(taskIdCounter, inputTitle, inputDescription)
                        myTasks.add(newTask)
                        taskIdCounter++

                        // Clean input boxes
                        inputTitle = ""
                        inputDescription = ""
                    }
                }
                //modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Añadir Tarea")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Tareas:", style = MaterialTheme.typography.titleMedium)
            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (myTasks.isEmpty()) {
                    Text(text = "No hay tareas pendientes")
                } else {
                    myTasks.forEachIndexed { index, task ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically // Centres everything vertically
                        ) {
                            // Checkbox: completed task
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { isChecked ->
                                    myTasks[index] = task.copy(isCompleted = isChecked)
                                }
                            )

                            // Tasks texts
                            Column(modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp)) {
                                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                                // Text(text = "ID: ${task.id}")
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            // Delete task button
                            Button(
                                onClick = { myTasks.removeAt(index)}
                            ) {
                                Text("Borrar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TraccoonTextField(
    value: String,
    onValueChange: (String) -> Unit, // Function that receives a String
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )
}