package com.hipno06.traccoon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.model.Task
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.compose.ui.text.style.TextOverflow
import com.hipno06.traccoon.model.generateTaskHash
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

    //? States to save what user writes inside text boxes
    var inputTitle by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }

    //? State to know if bottom task sheet is shown
    var showBottomSheet by remember { mutableStateOf(false) }
    //? Sheet animation controller
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Tareas:", style = MaterialTheme.typography.titleMedium)
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (myTasks.isEmpty()) {
                        Text(text = "No hay tareas pendientes")
                    } else {
                        myTasks.forEachIndexed { index, task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically // Centres everything vertically
                                ) {
                                    //? Checkbox: completed task
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { isChecked ->
                                            myTasks[index] = task.copy(isCompleted = isChecked)
                                            saveTasks()
                                        }
                                    )

                                    //? Tasks texts
                                    Column(
                                        modifier = Modifier.weight(1f)
                                            .padding(start = 8.dp, end = 8.dp)
                                    ) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                // Cross out the task's title if it's marked as completed
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            ),
                                            // Recolor the task's title in gray if it's marked as completed
                                            color = if (task.isCompleted) Color.Gray else Color.Unspecified,
                                            // Text Overflow
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis // "..." shows when the text overflows
                                        )
                                        // Text(text = "ID: ${task.id}")
                                        if (task.description.isNotBlank() && !task.isCompleted) {
                                            Text(
                                                text = task.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                // Text Overflow
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    //? Delete task button
                                    Button(
                                        onClick = {
                                            myTasks.removeAt(index)
                                            saveTasks()
                                        }
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
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    inputTitle = ""
                    inputDescription = ""
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .safeContentPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Nueva Tarea",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    //? Text Box: Title
                    TraccoonTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = "Título de la tarea"
                    )
                    //? Text Box: Description
                    TraccoonTextField(
                        value = inputDescription,
                        onValueChange = { inputDescription = it },
                        label = "Descripción (opcional)"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                inputTitle = ""
                                inputDescription = ""
                                showBottomSheet = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        //? Button: save task
                        Button(
                            onClick = {
                                // Only save if title isn't empty
                                if (inputTitle.isNotBlank()) {
                                    val newTask =
                                        Task(generateTaskHash(), inputTitle, inputDescription)
                                    myTasks.add(newTask)
                                    saveTasks()

                                    // Clean input boxes
                                    inputTitle = ""
                                    inputDescription = ""
                                    showBottomSheet = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Añadir Tarea")
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