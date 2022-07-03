package com.jp_funda.todomind.view.mind_map_create.options_sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.Task
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.components.MindMapOptionsTabRow
import com.jp_funda.todomind.view.components.TaskEditContent
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MindMapOptionsSheetScreen(bottomSheetState: BottomSheetState, mainViewModel: MainViewModel) {
    val addChildViewModel = hiltViewModel<AddChildViewModel>()

    LaunchedEffect(Unit) {
        // Set mind map to addChildViewModel
        mainViewModel.editingMindMap?.let { addChildViewModel.setMindMap(it) }
        // Set parentTask to addChildViewModel
        mainViewModel.selectedNode?.let { addChildViewModel.initializeParentTask(it) }
        // Set addChildViewModel's new task status as open
        addChildViewModel.setStatus(TaskStatus.Open)
    }

    MindMapOptionsSheetContent(bottomSheetState = bottomSheetState, mainViewModel = mainViewModel)
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MindMapOptionsSheetContent(bottomSheetState: BottomSheetState, mainViewModel: MainViewModel) {
    val sheetViewModel = hiltViewModel<MindMapOptionsViewModel>()
    val addChildViewModel = hiltViewModel<AddChildViewModel>()
    val editTaskViewModel = hiltViewModel<EditTaskViewModel>()

    val coroutineScope = rememberCoroutineScope()

    val observedMode = sheetViewModel.selectedMode.observeAsState()
    observedMode.value?.let { selectedMode ->
        Column(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .background(colorResource(id = R.color.deep_purple))
        ) {
            // When taskNode is Selected
            if (mainViewModel.selectedNode != null) {
                MindMapOptionsTabRow(selectedMode = selectedMode) {
                    sheetViewModel.setMode(it)
                }

                // Edit Task Option
                // set Editing Task
                mainViewModel.selectedNode?.let { editTaskViewModel.setEditingTask(it) }
                AnimatedVisibility(
                    visible = selectedMode == MindMapOptionsMode.EDIT_TASK,
                    enter = slideInHorizontally(
                        initialOffsetX = { -300 }, // small slide 300px
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = LinearEasing // interpolator
                        )
                    ),
                    exit = ExitTransition.None
                ) {
                    TaskEditContent(
                        taskEditableViewModel = editTaskViewModel,
                        mainViewModel = null,
                    ) {
                        coroutineScope.launch { bottomSheetState.collapse() }
                    }
                }
            } else { // When mindMap Node is selected
                TabRow(
                    modifier = Modifier.height(50.dp),
                    selectedTabIndex = selectedMode.ordinal,
                    backgroundColor = colorResource(id = R.color.transparent),
                    contentColor = Color.LightGray,
                ) {
                    Tab(
                        selected = true,
                        onClick = {},
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mind_map),
                                contentDescription = "Edit"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Add Child",
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                }
                TaskEditContent(
                    taskEditableViewModel = addChildViewModel,
                    mainViewModel = null,
                ) {
                    coroutineScope.launch { bottomSheetState.collapse() }
                }
            }

            // Add Child Option
            AnimatedVisibility(
                visible = selectedMode == MindMapOptionsMode.ADD_CHILD,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 }, // small slide 300px
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing // interpolator
                    )
                ),
                exit = ExitTransition.None
            ) {
                TaskEditContent(
                    taskEditableViewModel = addChildViewModel,
                    mainViewModel = null,
                ) {
                    coroutineScope.launch { bottomSheetState.collapse() }
                }
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
fun setUpAddingChildNode(
    selectedNode: Task?,
    mainViewModel: MainViewModel,
    addChildViewModel: AddChildViewModel,
) {
    selectedNode?.let { selectedTask ->
        addChildViewModel.setX((selectedTask.x ?: 0f) + 300)
        addChildViewModel.setY(selectedTask.y ?: 0f)
    } ?: run {
        addChildViewModel.setX((mainViewModel.editingMindMap?.x ?: 0f) + 300f)
        addChildViewModel.setY(mainViewModel.editingMindMap?.y ?: 0f)
    }
}