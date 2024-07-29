package com.app.scanner.ui.component

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.scanner.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteContainer(
    isSwipeToDeleteEnable: Boolean,
    item: Pair<Uri, String>,
    onDelete: (Pair<Uri, String>) -> Boolean,
    animationDuration: Int = 300,
    content: @Composable (Uri) -> Unit,
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart &&  onDelete(item)) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground()
            },
            dismissContent = { content(item.first) },
            directions = if (isSwipeToDeleteEnable) setOf(DismissDirection.EndToStart) else setOf()
        )
    }
}

@Composable
fun DeleteBackground(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.delete_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
    }
}