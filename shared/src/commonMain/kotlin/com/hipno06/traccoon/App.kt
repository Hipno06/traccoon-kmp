package com.hipno06.traccoon

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.hipno06.traccoon.data.TaskRepository.loadTasks
import com.hipno06.traccoon.data.TaskRepository.saveTasks
import com.hipno06.traccoon.model.Task
import com.russhwolf.settings.Settings
import com.hipno06.traccoon.model.generateTaskHash
import com.hipno06.traccoon.ui.components.AddTaskSheet
import com.hipno06.traccoon.ui.screens.MainScreen

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
        MainScreen(
            myTasks = myTasks,
            onSaveTasks = saveTasks,
            onOpenAddTaskSheet = { showBottomSheet = true }
        )

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