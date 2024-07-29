package com.app.scanner.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scanner.R

@Composable
fun InputCategory(
    onNegativeClick: () -> Unit,
    onPositiveClick: (String) -> Unit
) {
    var graphicVisible by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        graphicVisible = true
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedVisibility(
            visible = graphicVisible, enter = expandVertically(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                expandFrom = Alignment.CenterVertically,
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add category",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
                Text(
                    "Add category name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 24.sp
                )
                OutlinedTextField(
                    shape = RoundedCornerShape(8.dp),
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    label = {
                        Text(text = "Category", maxLines = 1)
                    },
                    placeholder = {
                        Text(text = "Add category", maxLines = 1)
                    },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Unspecified,
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Absolute.Right,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            TextButton(
                onClick = {
                    onNegativeClick()
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(shape = ButtonDefaults.textShape,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                onClick = {
                    if (categoryName.text.isNotEmpty())
                        onPositiveClick(categoryName.text)
                }) {
                Text(
                    "Save",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}