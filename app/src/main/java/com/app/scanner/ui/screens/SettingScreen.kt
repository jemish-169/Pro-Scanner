package com.app.scanner.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scanner.R
import com.app.scanner.viewModel.MainViewModel

@Composable
fun SettingScreen(
    viewModel: MainViewModel,
    innerPadding: PaddingValues,
    versionName: String,
    isAllowed: Boolean
) {

    var isSwipeToDeleteEnable by remember { mutableStateOf(viewModel.getIsSwipeToDeleteEnable()) }
    val categoryList by viewModel.categoryList.collectAsState()

    LaunchedEffect(Unit) {
        if (categoryList.isEmpty())
            viewModel.getCategories()
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
                text = "Settings",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }

//        SettingDropDownItem(
//            icon = R.drawable.ic_category,
//            title = "Document Categories",
//            categoryList = categoryList
//        )

        SettingSwitchItem(icon = Icons.Default.Delete,
            title = "Swipe Pdf item to delete",
            isChecked = isSwipeToDeleteEnable,
            onCheckedChange = {
                isSwipeToDeleteEnable = !isSwipeToDeleteEnable
                viewModel.setIsSwipeToDeleteEnable(isSwipeToDeleteEnable)
            })

        Text(
            text = "App information",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        AppInformationItem(
            icon = R.drawable.ic_info,
            title = "Files management in app",
            subtitle = "We are using ${if (isAllowed) "External" else "Internal"} storage to manage files."
        )

        AppInformationItem(
            icon = R.drawable.share_24,
            title = "Share Pro scanner app",
            subtitle = "Share app and make their life easy"
        )

        AppInformationItem(
            icon = R.drawable.star_24,
            title = "Rate this App",
            subtitle = "Rate app on play store"
        )

        AppInformationItem(
            icon = R.drawable.rounded_lock_24,
            title = "Privacy Policy",
            subtitle = "Read this app's privacy policy"
        )

        AppInformationItem(
            icon = R.drawable.round_commit_24, title = "Version Number", subtitle = versionName
        )
    }
}

@Composable
fun AppInformationItem(icon: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
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
fun SettingSwitchItem(
    icon: ImageVector, title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
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
            imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 16.dp)
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
fun SettingDropDownItem(
    icon: Int, title: String, categoryList: List<String>
) {
    var isOpened by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isOpened) 90f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "animate icon"
    )

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
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = title,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { isOpened = !isOpened }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
                    Text(
                        category, modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(0.1f),
                                shape = CircleShape
                            )
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), CircleShape
                            )
                            .padding(vertical = 4.dp, horizontal = 10.dp)
                    )
                }
            }
        }
    }
}