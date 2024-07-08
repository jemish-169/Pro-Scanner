package com.app.scanner.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scanner.R
import com.app.scanner.viewModel.MainViewModel

@Composable
fun SettingScreen(viewModel: MainViewModel, innerPadding: PaddingValues, versionName: String) {

    var isSwipeToDeleteEnable by remember { mutableStateOf(viewModel.getIsSwipeToDeleteEnable()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
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
        SettingSwitchItem(icon = Icons.Default.Delete,
            title = "Swipe Pdf item to delete",
            isChecked = isSwipeToDeleteEnable,
            onCheckedChange = {
                isSwipeToDeleteEnable = !isSwipeToDeleteEnable
                viewModel.setIsSwipeToDeleteEnable(isSwipeToDeleteEnable)
            })

        HorizontalDivider(thickness = 1.dp)

        Text(
            text = "App information",
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
                .padding(top = 24.dp, bottom = 8.dp)
        )

        AppInformationItem(
            icon = R.drawable.share_24,
            title = "Share Pro scanner app",
            subtitle = "Share app with others and make their life easy"
        )
        AppInformationItem(
            icon = R.drawable.star_24, title = "Rate this App", subtitle = "Rate app on play store"
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
            Text(text = title, fontSize = 18.sp)
            Text(text = subtitle, fontSize = 14.sp, style = MaterialTheme.typography.bodySmall)
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
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
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
