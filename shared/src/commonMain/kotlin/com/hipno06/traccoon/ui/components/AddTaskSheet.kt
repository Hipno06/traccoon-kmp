package com.hipno06.traccoon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onAddTask: (String, String) -> Unit
) {
    //? Sheet animation controller
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    //? States to save what user writes inside text boxes
    var inputTitle by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                modifier = Modifier.padding(top = 16.dp, bottom = 5.dp),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
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
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                //? Button: save task
                Button(
                    onClick = {
                        // Only save if title isn't empty
                        if (inputTitle.isNotBlank()) {
                            onAddTask(inputTitle, inputDescription)
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