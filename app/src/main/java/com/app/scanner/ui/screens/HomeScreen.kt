package com.app.scanner.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.app.scanner.R
import com.app.scanner.activity.ViewPdfActivity
import com.app.scanner.ui.component.CircleCheckbox
import com.app.scanner.ui.component.CustomDialog
import com.app.scanner.ui.component.DialogContent
import com.app.scanner.ui.component.SwipeToDeleteContainer
import com.app.scanner.util.pdfToBitmap
import com.app.scanner.util.shareSelectedFiles
import com.app.scanner.viewModel.MainViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

private const val storagePermissionCode = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    context: Activity,
    innerPadding: PaddingValues,
    isShowDialog: Int,
    isSwipeToDeleteEnable: Boolean,
    onEditClick: (Uri) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val documentList by viewModel.documentList.collectAsState()
    var showDialog by remember { mutableIntStateOf(isShowDialog) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val selectedItems = remember { mutableStateOf(setOf<Uri>()) }

    LaunchedEffect(Unit) {
        if (documentList.isEmpty()) viewModel.getFilesIfNeeded()
    }
    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) focusRequester.requestFocus()
    }

    if (showDialog == 1) {
        CustomDialog(onDismissRequest = { showDialog = 0 }) {
            DialogContent(icon = Icons.Rounded.Warning,
                iconTint = MaterialTheme.colorScheme.onSurface,
                iconDesc = "Warning",
                titleText = "Need Permission!",
                descText = "Pro scanner requires permission to manage PDF files.",
                positiveBtn = "Allow",
                negativeBtn = "Deny",
                onNegativeClick = { showDialog = 0 },
                onPositiveClick = {
                    showDialog = 0
                    askPermission(context)
                })
        }
    } else if (showDialog == 2) {
        CustomDialog(onDismissRequest = { showDialog = 0 }) {
            DialogContent(icon = Icons.Rounded.Delete,
                iconTint = MaterialTheme.colorScheme.error,
                iconDesc = "Delete?",
                titleText = "Are you sure?",
                descText = "Do you want to delete ${selectedItems.value.size} ${if (selectedItems.value.size == 1) "file?" else "files"}",
                positiveBtn = "Continue",
                negativeBtn = "Cancel",
                onNegativeClick = {
                    showDialog = 0
                    selectedItems.value = emptySet()
                },
                onPositiveClick = {
                    showDialog = 0
                    if (viewModel.deleteSelectedFiles(
                            context, selectedItems.value.toList()
                        )
                    ) Toast.makeText(
                        context,
                        "${if (selectedItems.value.size == 1) "File" else "Files"} deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    else Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    selectedItems.value = emptySet()
                })
        }
    }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            AnimatedVisibility(
                visible = selectedItems.value.isNotEmpty(),
                enter = slideInHorizontally(initialOffsetX = { -it })
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                selectedItems.value = emptySet()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "Back icon",
                            )
                        }
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = "${selectedItems.value.size} ${if (selectedItems.value.size == 1) "file" else "files"} selected",
                            fontSize = 20.sp,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedVisibility(
                            visible = selectedItems.value.size == 1,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                onClick = {
                                    onEditClick(selectedItems.value.first())
                                    selectedItems.value = emptySet()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "Edit icon",
                                )
                            }
                        }
                        IconButton(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            onClick = {
                                shareSelectedFiles(context, selectedItems.value.toList())
                                selectedItems.value = emptySet()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "share icon",
                            )
                        }
                        IconButton(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            onClick = { showDialog = 2 },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "delete icon",
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = !isSearchVisible, enter = slideInHorizontally(initialOffsetX = { -it })
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                ) {
                    Text(
                        text = "Pro Scanner",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { isSearchVisible = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            modifier = Modifier.size(28.dp),
                            contentDescription = "search icon",
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isSearchVisible, enter = slideInHorizontally(initialOffsetX = { it })
            ) {
                TextField(
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    value = searchText,
                    onValueChange = { searchText = it },
                    leadingIcon = {
                        IconButton(onClick = {
                            isSearchVisible = false
                            searchText = TextFieldValue("")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "search field back arrow icon",
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    placeholder = {
                        Text(text = "Search a file", maxLines = 1)
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .height(88.dp)
                        .padding(vertical = 16.dp)
                        .focusRequester(focusRequester = focusRequester)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }
        if (documentList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_document_placeholder),
                    contentDescription = "No Documents",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You don't have any documents",
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(key = { it }, items = documentList.filter { uri ->
                    uri.toFile().name.contains(searchText.text)
                }) {
                    SwipeToDeleteContainer(isSwipeToDeleteEnable = isSwipeToDeleteEnable,
                        item = it,
                        content = {
                            ItemPdf(
                                context = context,
                                uri = it,
                                onItemClick = { fileName ->
                                    if (selectedItems.value.isNotEmpty()) {
                                        val newSelection = selectedItems.value.toMutableSet()
                                        if (newSelection.contains(it)) {
                                            newSelection.remove(it)
                                        } else {
                                            newSelection.add(it)
                                        }
                                        selectedItems.value = newSelection
                                    } else {
                                        val intent = Intent(context, ViewPdfActivity::class.java)
                                        intent.putExtra("SelectedFile", it.toString())
                                        intent.putExtra(
                                            "SelectedFileName", fileName
                                        )
                                        context.startActivity(intent)
                                    }
                                },
                                onItemLongClick = {
                                    if (selectedItems.value.isEmpty()) {
                                        selectedItems.value = setOf(it)
                                    } else {
                                        val newSelection = selectedItems.value.toMutableSet()
                                        if (newSelection.contains(it)) {
                                            newSelection.remove(it)
                                        } else {
                                            newSelection.add(it)
                                        }
                                        selectedItems.value = newSelection
                                    }
                                },
                                isSelected = selectedItems.value.contains(it),
                                isInSelectionMode = selectedItems.value.isNotEmpty()
                            )
                        },
                        onDelete = { uri ->
                            if (viewModel.deleteSelectedFiles(context, listOf(uri))) Toast.makeText(
                                context, "File deleted", Toast.LENGTH_SHORT
                            ).show()
                            else Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemPdf(
    context: Activity,
    uri: Uri,
    onItemClick: (String) -> Unit,
    onItemLongClick: () -> Unit,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
) {
    val file = uri.toFile()
    val image = pdfToBitmap(file, context = context)
    Card(
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { onItemClick(file.name.toString()) }, onLongClick = onItemLongClick
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                bitmap = image.first,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(36.dp)
            )
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                ) {
                    Text(
                        text = file.name,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                    if (isInSelectionMode) {
                        CircleCheckbox(selected = isSelected,
                            onChecked = { onItemClick(file.name.toString()) })
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = image.third,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Text(
                        text = image.second.toString() + " Pages",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Text(
                        text = "category",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                BorderStroke(
                                    width = 0.7.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ), CircleShape
                            )
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

fun scanDoc(
    context: Activity,
    scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    val options = GmsDocumentScannerOptions.Builder().setScannerMode(SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true).setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .build()
    val scanner = GmsDocumentScanning.getClient(options)
    scanner.getStartScanIntent(context).addOnSuccessListener {
        scannerLauncher.launch(
            IntentSenderRequest.Builder(it).build()
        )
    }.addOnFailureListener {
        Toast.makeText(
            context, "Something went wrong!", Toast.LENGTH_SHORT
        ).show()
    }
}

private fun askPermission(context: Activity): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val uri = Uri.parse("package:${context.packageName}")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            context.startActivity(intent)
        } else return true
    } else {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storagePermissionCode
            )
        } else return true
    }
    return false
}