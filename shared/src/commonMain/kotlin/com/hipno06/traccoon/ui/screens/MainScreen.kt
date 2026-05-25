package com.hipno06.traccoon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.model.Task
import com.hipno06.traccoon.ui.components.TaskCard

@Composable
fun MainScreen(
    myTasks: MutableList<Task>,
    onSaveTasks: () -> Unit,
    onOpenAddTaskSheet: () -> Unit
) {
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddTaskSheet,
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
                                onSaveTasks()
                            },
                            onDeleteClick = {
                                myTasks.removeAt(index)
                                onSaveTasks()
                            }
                        )
                    }
                }
            }
        }
    }
}