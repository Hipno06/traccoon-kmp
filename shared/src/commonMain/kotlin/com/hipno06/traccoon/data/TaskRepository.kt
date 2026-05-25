package com.hipno06.traccoon.data

import com.hipno06.traccoon.model.Task
import com.hipno06.traccoon.model.generateTaskHash
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

object TaskRepository {
    private const val KEY_TASKS = "MIS_TAREAS"

    //? Save function (we'll use it when pressing a button)
    fun saveTasks(settings: Settings?, tasks: List<Task>) {
        if (settings == null) return
        val jsonString = Json.encodeToString(tasks)
        settings.putString(KEY_TASKS, jsonString)
    }

    fun loadTasks(settings: Settings?): List<Task> {
        if (settings == null) return emptyList()

        val savedJson = settings.getString(KEY_TASKS, "")
        if (savedJson.isEmpty()) return emptyList()
        //? Json -> String
        return try {
            //? Try to load tasks from JSON
            Json.decodeFromString<List<Task>>(savedJson)
        } catch (_: Exception) {
            //! Old JSON format detected
            migrateAndLoad(settings, savedJson)
        }
    }

    private fun migrateAndLoad(settings: Settings, savedJson: String): List<Task> {
        return try {
            // Read the raw file
            val jsonElement = Json.parseToJsonElement(savedJson)
            val migratedList = mutableListOf<Task>()

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
                val loadedTasks = Json.decodeFromJsonElement<List<Task>>(migratedArray)
                migratedList.addAll(loadedTasks)

                // Overwrite the old file with new changes
                saveTasks(settings, migratedList)
            }
            migratedList
        } catch (_: Exception) {
            //! If there's an error, delete the file
            settings.remove(KEY_TASKS)
            emptyList()
        }
    }
}
