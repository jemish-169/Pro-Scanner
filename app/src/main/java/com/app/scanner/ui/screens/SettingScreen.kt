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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scanner.R

@Composable
fun SettingScreen(innerPadding: PaddingValues, versionName: String) {
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
        SettingsItem(
            icon = R.drawable.share_24,
            title = "Share Pro scanner app",
            subtitle = "Share app with others and make their life easy"
        )
        SettingsItem(
            icon = R.drawable.star_24,
            title = "Rate this App",
            subtitle = "Rate app on play store"
        )
        SettingsItem(
            icon = R.drawable.rounded_lock_24,
            title = "Privacy Policy",
            subtitle = "Read this app's privacy policy"
        )
        SettingsItem(
            icon = R.drawable.round_commit_24,
            title = "Version Number",
            subtitle = versionName
        )
    }
}

@Composable
fun SettingsItem(icon: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
            Text(text = subtitle, fontSize = 14.sp)
        }
    }
}