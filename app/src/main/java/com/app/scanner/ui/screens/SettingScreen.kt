package com.app.scanner.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scanner.R
import com.app.scanner.ui.component.CustomDialog
import com.app.scanner.ui.component.DialogContent
import com.app.scanner.ui.component.InputCategory
import com.app.scanner.util.ThemeOption
import com.app.scanner.viewModel.MainViewModel


@Composable
fun SettingScreen(
    context: Activity,
    viewModel: MainViewModel,
    innerPadding: PaddingValues,
    versionName: String,
    isAllowed: Boolean,
    askPermission: () -> Unit
) {

    var isSwipeToDeleteEnable by remember { mutableStateOf(viewModel.getIsSwipeToDeleteEnable()) }
    val categoryList by viewModel.categoryList.collectAsState()

    LaunchedEffect(Unit) {
        if (categoryList.isEmpty()) viewModel.getCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        SettingCategoryItems(context = context,
            icon = R.drawable.category,
            title = stringResource(R.string.document_categories),
            categoryList = categoryList,
            addCategory = { category ->
                viewModel.addCategoryInList(category)
            },
            onRemoveCategory = { category ->
                viewModel.removeCategoryFromList(category)
            })

        SettingSwitchItem(icon = R.drawable.delete_24,
            title = stringResource(R.string.swipe_pdf_item_to_delete),
            isChecked = isSwipeToDeleteEnable,
            onCheckedChange = {
                isSwipeToDeleteEnable = !isSwipeToDeleteEnable
                viewModel.setIsSwipeToDeleteEnable(isSwipeToDeleteEnable)
            })

        ThemeSelector(viewModel)

        Text(
            text = stringResource(R.string.app_information),
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        AppInfoAnnotatedItem(
            icon = R.drawable.info,
            title = stringResource(R.string.files_management_in_app),
            subtitle = "We are using ${if (isAllowed) "External" else "Internal"} storage to manage files.",
            extraString = if (!isAllowed) stringResource(R.string.give_permission) else "",
            askPermission = askPermission
        )

        AppInformationItem(
            icon = R.drawable.share_24,
            title = stringResource(R.string.share_pro_scanner_app),
            subtitle = stringResource(R.string.share_app_and_make_their_life_easy),
            onClick = {
                val appName = context.getString(R.string.app_name)
                val textIntent = Intent(Intent.ACTION_SEND)
                textIntent.type = "text/plain"

                textIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
                val shareText = context.getString(R.string.share_app_text, context.packageName)
                textIntent.putExtra(Intent.EXTRA_TEXT, shareText)

                val chooserIntent = Intent.createChooser(textIntent, appName)
                context.startActivity(chooserIntent)
            }
        )

        AppInformationItem(
            icon = R.drawable.star_24,
            title = stringResource(R.string.rate_this_app),
            subtitle = stringResource(R.string.rate_app_on_play_store),
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            context.getString(
                                R.string.play_store_app_link,
                                context.packageName
                            )
                        )
                    )
                )
            }
        )

        AppInformationItem(
            icon = R.drawable.rounded_lock_24,
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.read_this_app_s_privacy_policy),
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            context.getString(
                                R.string.privacy_policy_link
                            )
                        )
                    )
                )
            }
        )

        AppInformationItem(
            icon = R.drawable.round_commit_24,
            title = stringResource(R.string.version_number),
            subtitle = versionName,
            onClick = {
                Toast.makeText(
                    context,
                    context.getString(R.string.version_number) + ": $versionName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currentTheme by viewModel.theme.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
            .border(
                BorderStroke(0.7.dp, MaterialTheme.colorScheme.secondary), RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.select_theme),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = stringResource(R.string.current_theme), modifier = Modifier.weight(1f)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(modifier = Modifier
                .menuAnchor()
                .width(160.dp),
                readOnly = true,
                value = currentTheme.displayName,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ThemeOption.entries.forEach { themeOption ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.setTheme(themeOption)
                            expanded = false
                        },
                        text = {
                            Text(text = themeOption.displayName)
                        },
                        enabled = themeOption.name != ThemeOption.DYNAMIC.name || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    )
                }
            }
        }
    }
}

@Composable
fun AppInformationItem(icon: Int, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun AppInfoAnnotatedItem(
    icon: Int, title: String, subtitle: String, extraString: String, askPermission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
            )
            Text(
                text = subtitle, fontSize = 14.sp, style = MaterialTheme.typography.bodySmall
            )
            if (extraString.isNotEmpty()) {
                Text(
                    text = extraString,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight(450),
                    modifier = Modifier.clickable(onClick = { askPermission() })
                )
            }
        }
    }
}

@Composable
fun SettingSwitchItem(
    icon: Int, title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
            .border(
                BorderStroke(0.7.dp, MaterialTheme.colorScheme.secondary), RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isChecked, onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingCategoryItems(
    context: Activity,
    icon: Int,
    title: String,
    categoryList: List<String>,
    addCategory: (String) -> Unit,
    onRemoveCategory: (String) -> Unit
) {
    var isOpened by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    val rotation by animateFloatAsState(
        targetValue = if (isOpened) 90f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = stringResource(R.string.animate_icon)
    )

    var showDialog by remember { mutableIntStateOf(0) }

    if (showDialog == 1) {
        CustomDialog(onDismissRequest = { }) {
            InputCategory(onPositiveClick = { category ->
                showDialog = 0
                if (categoryList.contains(category)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.category_already_exists),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    addCategory(category)
                }
            }, onNegativeClick = {
                showDialog = 0
            })
        }
    } else if (showDialog == 2) {
        CustomDialog(onDismissRequest = { showDialog = 0 }) {
            DialogContent(icon = R.drawable.delete_24,
                iconTint = MaterialTheme.colorScheme.error,
                iconDesc = stringResource(R.string.delete),
                titleText = stringResource(
                    R.string.are_you_sure_you_want_to_delete, selectedCategory
                ),
                descText = stringResource(R.string.do_not_worry_your_files_will_not_be_deleted),
                positiveBtn = stringResource(R.string.delete_word),
                negativeBtn = stringResource(R.string.cancel),
                onNegativeClick = {
                    showDialog = 0
                },
                onPositiveClick = {
                    showDialog = 0
                    onRemoveCategory(selectedCategory)
                })
        }

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                BorderStroke(0.7.dp, MaterialTheme.colorScheme.secondary), RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = title,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { isOpened = !isOpened }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotation)
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isOpened, enter = expandVertically(), exit = shrinkVertically()
        ) {
            FlowRow {
                categoryList.forEach { category ->
                    CategoryItem(category = category,
                        icon = R.drawable.close,
                        contentDesc = stringResource(R.string.remove),
                        onClick = {
                            selectedCategory = category
                            showDialog = 2
                        })
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .clickable { showDialog = 1 }
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(0.0f),
                            shape = CircleShape
                        )
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), CircleShape
                        )
                        .padding(vertical = 4.dp, horizontal = 10.dp)) {
                    Text(text = stringResource(R.string.add_category))
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = stringResource(R.string.add_category),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clip(CircleShape)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: String, icon: Int, contentDesc: String, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(0.0f), shape = CircleShape
            )
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), CircleShape
            )
            .padding(vertical = 4.dp, horizontal = 10.dp)
    ) {
        Text(text = category)
        Icon(painter = painterResource(icon),
            contentDescription = contentDesc,
            modifier = Modifier
                .padding(start = 4.dp)
                .clip(CircleShape)
                .clickable { onClick() }
                .size(20.dp))
    }
}