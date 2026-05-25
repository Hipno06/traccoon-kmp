package com.hipno06.traccoon.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hipno06.traccoon.model.Task

@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
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
                onCheckedChange = onCheckedChange
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
                onClick = onDeleteClick
            ) {
                Text("Borrar")
            }
        }
    }
}