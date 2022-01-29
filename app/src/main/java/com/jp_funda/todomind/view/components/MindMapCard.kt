package com.jp_funda.todomind.view.components

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.jp_funda.todomind.R

@Composable
fun MindMapCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    AndroidView(
        factory = {
            View.inflate(it, R.layout.card_mind_map, null)
        },
        modifier = modifier.clickable {
            onClick()
        }
    )
}