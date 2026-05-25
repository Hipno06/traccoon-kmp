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
import com.hipno06.traccoon.model.Task
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.hipno06.traccoon.model.generateTaskHash
import com.hipno06.traccoon.ui.components.AddTaskSheet
import com.hipno06.traccoon.ui.components.TaskCard
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    //? Task list
    val myTasks = remember { mutableStateListOf<Task>() }

    val isPreview = LocalInspectionMode.current
    //? Save logic
    // Multiplatform save tool
    val settings = remember { if (isPreview) null else Settings() }
    //? Save function (we'll use it when pressing a button)
    val saveTasks = {
        if (!isPreview) {   // Only saves if it's real
            // String -> JSON
            val jsonString = Json.encodeToString(myTasks.toList())
            settings?.putString("MIS_TAREAS", jsonString)
        }
    }
    //? Load tasks when the app opens
    LaunchedEffect(Unit) {
        if (!isPreview) {   // Only loads if it's real
            val savedJson = settings?.getString("MIS_TAREAS", "") ?: ""
            if (savedJson.isNotEmpty()) {
                // Json -> String
                try {
                    // Try to load tasks from JSON
                    val loadTasks = Json.decodeFromString<List<Task>>(savedJson)
                    myTasks.addAll(loadTasks)
                } catch (_: Exception) {
                    // Old JSON format detected
                    try {
                        // Read the raw file
                        val jsonElement = Json.parseToJsonElement(savedJson)
                        if (jsonElement is JsonArray) {
                            // Build another JSON element by element
                            val migratedArray = buildJsonArray {
                                for (taskElement in jsonElement) {
                                    if (taskElement is JsonObject) {
                                        val mutableTask = taskElement.toMutableMap()
                                        // Check the old ID
                                        val oldId = mutableTask["id"]
                                        // If the old ID isn't a String, change it and generate a new task hash
                                        if (oldId?.jsonPrimitive?.isString == false) {
                                            mutableTask["id"] = JsonPrimitive(generateTaskHash())
                                        }
                                        // Add the task to the list
                                        add(JsonObject(mutableTask))
                                    }
                                }
                            }
                            // converts the new JSON into a task
                            val loadTasks = Json.decodeFromJsonElement<List<Task>>(migratedArray)
                            myTasks.addAll(loadTasks)

                            // Overwrite the old file with new changes
                            val correctedJsonString = Json.encodeToString(myTasks.toList())
                            settings?.putString("MIS_TAREAS", correctedJsonString)
                        }
                    } catch (_: Exception) {
                        //! If there's an error, delete the file
                        settings?.remove("MIS_TAREAS")
                    }
                }
            }
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