package com.elite.scanner.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import com.elite.scanner.R
import com.elite.scanner.activity.ViewPdfActivity
import com.elite.scanner.ui.component.CircleCheckbox
import com.elite.scanner.ui.component.CustomDialog
import com.elite.scanner.ui.component.DialogContent
import com.elite.scanner.ui.component.SwipeToDeleteContainer
import com.elite.scanner.util.Constants.Companion.ALL
import com.elite.scanner.util.Constants.Companion.SELECTED_FILES
import com.elite.scanner.util.Constants.Companion.SELECTED_FILE_NAME
import com.elite.scanner.util.formatFileSize
import com.elite.scanner.util.pdfToBitmap
import com.elite.scanner.util.shareSelectedFiles
import com.elite.scanner.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    context: Activity,
    innerPadding: PaddingValues,
    isShowDialog: Int,
    isSwipeToDeleteEnable: Boolean,
    onEditClick: (Pair<Uri, String>) -> Unit,
    duplicateFile: (Pair<Uri, String>) -> Unit,
    askFileSaveLocation: (Uri) -> Boolean,
    saveFileAsImages: (Pair<Uri, String>) -> Boolean,
    onPositiveClick: () -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val documentList by viewModel.documentList.collectAsState()
    var showDialog by remember { mutableIntStateOf(isShowDialog) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isSelectionMode by remember { mutableStateOf(false) }
    var isFileDeleted = false
    val selectedItems = remember { mutableStateOf(setOf<Pair<Uri, String>>()) }
    val categoryList by viewModel.categoryList.collectAsState()
    var selectedCategory by remember { mutableStateOf(ALL) }

    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) focusRequester.requestFocus()
    }
    if (showDialog == 1) {
        CustomDialog(onDismissRequest = { showDialog = 0 }) {
            DialogContent(icon = R.drawable.warning_24,
                iconTint = MaterialTheme.colorScheme.onSurface,
                iconDesc = stringResource(R.string.warning),
                titleText = stringResource(R.string.need_permission),
                descText = stringResource(R.string.pro_scanner_requires_permission_to_manage_pdf_files),
                positiveBtn = stringResource(R.string.allow),
                negativeBtn = stringResource(R.string.deny),
                onNegativeClick = { showDialog = 0 },
                onPositiveClick = {
                    showDialog = 0
                    onPositiveClick()
                })
        }
    } else if (showDialog == 2) {
        CustomDialog(onDismissRequest = { showDialog = 0 }) {
            DialogContent(icon = R.drawable.delete_24,
                iconTint = MaterialTheme.colorScheme.error,
                iconDesc = stringResource(R.string.delete),
                titleText = stringResource(R.string.are_you_sure),
                descText = "Do you want to delete ${selectedItems.value.size} ${if (selectedItems.value.size == 1) "file?" else "files"}",
                positiveBtn = stringResource(R.string.continue_word),
                negativeBtn = stringResource(R.string.cancel),
                onNegativeClick = {
                    showDialog = 0
                    selectedItems.value = emptySet()
                    isSelectionMode = false
                    isFileDeleted = false
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
                    else Toast.makeText(
                        context,
                        context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                    ).show()
                    selectedItems.value = emptySet()
                    isSelectionMode = false
                    isFileDeleted = true
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
                visible = isSelectionMode,
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
                                isSelectionMode = false
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back),
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = stringResource(id = R.string.back_icon),
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
                                    isSelectionMode = false
                                },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.rename),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = stringResource(R.string.edit_icon),
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
                                isSelectionMode = false
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.share_24),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = stringResource(R.string.share_icon),
                            )
                        }
                        IconButton(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            onClick = { showDialog = 2 },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete_24),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = stringResource(R.string.delete_icon),
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
                        text = stringResource(id = R.string.app_name),
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
                            contentDescription = stringResource(R.string.search_icon),
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
                        capitalization = KeyboardCapitalization.Unspecified,
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
                                painter = painterResource(id = R.drawable.arrow_back),
                                contentDescription = stringResource(R.string.back_icon)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    placeholder = {
                        Text(text = stringResource(R.string.search_a_file), maxLines = 1)
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
        Row(modifier = Modifier.padding(bottom = 4.dp)) {
            val colorAll = if (selectedCategory == ALL) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
            val bgColorAll =
                if (selectedCategory == ALL) colorAll.copy(0.1f) else colorAll.copy(0.0f)
            Text(color = colorAll,
                text = ALL,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(
                        color = bgColorAll,
                    )
                    .border(
                        BorderStroke(
                            width = 1.dp, color = colorAll
                        ), CircleShape
                    )
                    .clickable { selectedCategory = ALL }
                    .padding(vertical = 4.dp, horizontal = 10.dp))
            LazyRow {
                items(key = { it }, items = categoryList) { category ->
                    val color = if (selectedCategory == category) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                    val bgColor =
                        if (selectedCategory == category) color.copy(0.1f) else color.copy(0.0f)
                    Text(color = color,
                        text = category,
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                color = bgColor,
                            )
                            .border(
                                BorderStroke(
                                    width = 1.dp, color = color
                                ), CircleShape
                            )
                            .clickable { selectedCategory = category }
                            .padding(vertical = 4.dp, horizontal = 10.dp))
                }
            }
        }
        val filteredList = documentList.filter { item ->
            val nameMatches = item.first.lastPathSegment.toString().contains(searchText.text)
            val categoryMatches = selectedCategory == ALL || item.second == selectedCategory
            nameMatches && categoryMatches
        }
        if (filteredList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_document_placeholder),
                    contentDescription = stringResource(R.string.no_documents),
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.you_don_t_have_any_documents),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(key = { it.first }, items = filteredList) { item ->
                    SwipeToDeleteContainer(isSwipeToDeleteEnable = !isSelectionMode && isSwipeToDeleteEnable,
                        item = item,
                        content = { contentUri ->
                            ItemPdf(
                                uri = contentUri,
                                onItemClick = { fileName ->
                                    if (isSelectionMode) {
                                        val newSelection = selectedItems.value.toMutableSet()
                                        if (newSelection.contains(item)) {
                                            newSelection.remove(item)
                                        } else {
                                            newSelection.add(item)
                                        }
                                        selectedItems.value = newSelection
                                        isSelectionMode = newSelection.isNotEmpty()
                                    } else {
                                        val intent = Intent(context, ViewPdfActivity::class.java)
                                        intent.putExtra(SELECTED_FILES, contentUri.toString())
                                        intent.putExtra(
                                            SELECTED_FILE_NAME, fileName
                                        )
                                        context.startActivity(intent)
                                    }
                                },
                                onClickSelectItem = {
                                    if (!isSelectionMode) {
                                        selectedItems.value = setOf(item)
                                        isSelectionMode = true
                                    } else {
                                        val newSelection = selectedItems.value.toMutableSet()
                                        if (newSelection.contains(item)) {
                                            newSelection.remove(item)
                                        } else {
                                            newSelection.add(item)
                                        }
                                        selectedItems.value = newSelection
                                        isSelectionMode = newSelection.isNotEmpty()
                                    }
                                },
                                onEditClick = { onEditClick(item) },
                                isSelected = selectedItems.value.contains(item),
                                isInSelectionMode = isSelectionMode,
                                duplicateFile = { duplicateFile(item) },
                                shareFile = { shareSelectedFiles(context, listOf(item)) },
                                deleteFile = {
                                    selectedItems.value = setOf(item)
                                    showDialog = 2
                                },
                                askFileSaveLocation = {
                                    if (!askFileSaveLocation(item.first)) showDialog = 1
                                },
                                saveFileAsImages = { if (!saveFileAsImages(item)) showDialog = 1 }
                            )
                        },
                        onDelete = { uri ->
                            selectedItems.value = setOf(uri)
                            showDialog = 2
                            isFileDeleted
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemPdf(
    uri: Uri,
    onItemClick: (String) -> Unit,
    onClickSelectItem: () -> Unit,
    isSelected: Boolean,
    onEditClick: () -> Unit,
    isInSelectionMode: Boolean,
    saveFileAsImages: () -> Unit,
    duplicateFile: () -> Unit,
    shareFile: () -> Unit,
    deleteFile: () -> Unit,
    askFileSaveLocation: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var anchorPositionX by remember { mutableIntStateOf(0) }

    val file = uri.toFile()
    val image = pdfToBitmap(file)

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false },
        offset = DpOffset(x = anchorPositionX.dp, y = 0.dp)
    ) {
        DropdownMenuItem(onClick = {
            isExpanded = false
            onClickSelectItem()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.check_circle_24,
                stringResource(R.string.select_icon),
                stringResource(R.string.select)
            )
        })
        DropdownMenuItem(onClick = {
            isExpanded = false
            onEditClick()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.rename,
                stringResource(R.string.rename_icon),
                stringResource(R.string.rename)
            )
        })
        DropdownMenuItem(onClick = {
            isExpanded = false
            askFileSaveLocation()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.download_24,
                stringResource(R.string.save_to_storage_icon),
                stringResource(R.string.save_to_storage)
            )
        })
        DropdownMenuItem(onClick = {
            isExpanded = false
            saveFileAsImages()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.save_images,
                stringResource(R.string.save_image_icon),
                stringResource(R.string.save_as_images)
            )
        })
        DropdownMenuItem(
            modifier = Modifier.padding(0.dp),
            onClick = {
                isExpanded = false
                duplicateFile()
            },
            text = {
                DropDownItemNameAndIcon(
                    R.drawable.copy_24,
                    stringResource(R.string.duplicate_icon),
                    stringResource(R.string.duplicate)
                )
            })
        DropdownMenuItem(onClick = {
            isExpanded = false
            shareFile()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.share_24,
                stringResource(R.string.share_icon),
                stringResource(R.string.share)
            )
        })
        DropdownMenuItem(onClick = {
            isExpanded = false
            deleteFile()
        }, text = {
            DropDownItemNameAndIcon(
                R.drawable.delete_24,
                stringResource(R.string.delete_icon),
                stringResource(R.string.delete_word)
            )
        })
    }

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
                onClick = { onItemClick(file.name.toString()) }, onLongClick = onClickSelectItem
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.pdf_icon),
                contentDescription = null,
                alpha = 0.7f,
                modifier = Modifier
                    .height(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(48.dp)
                    .padding(3.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f)
            ) {
                Text(
                    text = uri.lastPathSegment.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = image.second,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Text(
                        text = image.first.toString() + " Pages",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Text(
                        text = formatFileSize(file.length()),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .width(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isInSelectionMode) {
                    CircleCheckbox(
                        selected = isSelected,
                        onChecked = { onItemClick(file.name.toString()) })
                } else {
                    IconButton(modifier = Modifier.size(28.dp), onClick = {
                        isExpanded = true
                        anchorPositionX = with(density) { -6.dp.roundToPx() }
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.morevertical),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DropDownItemNameAndIcon(icon: Int, contentDesc: String, content: String) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            color = MaterialTheme.colorScheme.onSurface, text = content
        )
    }
}