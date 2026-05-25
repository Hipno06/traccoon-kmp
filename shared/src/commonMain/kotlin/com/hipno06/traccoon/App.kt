package com.hipno06.traccoon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.data.TaskRepository.loadTasks
import com.hipno06.traccoon.data.TaskRepository.saveTasks
import com.hipno06.traccoon.model.Task
import com.russhwolf.settings.Settings
import com.hipno06.traccoon.model.generateTaskHash
import com.hipno06.traccoon.ui.components.AddTaskSheet
import com.hipno06.traccoon.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    //? Task list
    val myTasks = remember { mutableStateListOf<Task>() }

    val isPreview = LocalInspectionMode.current
    val settings = remember { if (isPreview) null else Settings() }

    //? Load tasks when the app opens
    LaunchedEffect(Unit) {
        if (!isPreview) {   //? Only loads if it's real
            myTasks.addAll(loadTasks(settings))
        }
    }
    val saveTasks = {
        if (!isPreview) {
            saveTasks(settings, myTasks.toList())
        }
    }

    //? State to know if bottom task sheet is shown
    var showBottomSheet by remember { mutableStateOf(false) }
    MaterialTheme {
        //! --- MAIN SCREEN ---
        androidx.compose.material3.Scaffold(
            floatingActionButton = {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) { Text("+", style = MaterialTheme.typography.titleLarge) }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //? Title
                Text(
                    text = "🦝 Traccoon 🦝",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tareas:",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (myTasks.isEmpty()) {
                        Text(
                            text = "No hay tareas pendientes",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.W500
                        )
                    } else {
                        myTasks.forEachIndexed { index, task ->
                            TaskCard(
                                task = task,
                                onCheckedChange = { isChecked ->
                                    myTasks[index] = task.copy(isCompleted = isChecked)
                                    saveTasks()
                                },
                                onDeleteClick = {
                                    myTasks.removeAt(index)
                                    saveTasks()
                                }
                            )
                        }
                    }
                }
            }
        }
        if (showBottomSheet) {
            AddTaskSheet(
                onDismiss = {showBottomSheet = false},
                onAddTask = {title, description ->
                    val newTask = Task(
                        id = generateTaskHash(),
                        title = title,
                        description = description
                    )
                    myTasks.add(newTask)
                    saveTasks()
                    showBottomSheet = false
                }
            )
        }
    }
}