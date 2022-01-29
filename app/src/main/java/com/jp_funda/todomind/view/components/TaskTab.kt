package com.jp_funda.todomind.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.task.TaskViewModel

@Composable
fun TaskTab(selectedTabIndex: Int, onTabChange: (clickedTabIndex: TaskStatus) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = colorResource(id = R.color.deep_purple),
        contentColor = Color.White,
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabChange(TaskStatus.InProgress) },
            text = { Text("In Progress") }
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabChange(TaskStatus.Open) },
            text = { Text("Open") }
        )
        Tab(
            selected = selectedTabIndex == 2,
            onClick = { onTabChange(TaskStatus.Complete) },
            text = { Text("Closed") }
        )
    }
}