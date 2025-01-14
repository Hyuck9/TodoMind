package com.jp_funda.todomind.view.mind_map_detail

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.jp_funda.repositories.mind_map.entity.MindMap
import com.jp_funda.repositories.task.entity.NodeStyle
import com.jp_funda.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.Constant
import com.jp_funda.todomind.R
import com.jp_funda.todomind.TestTag
import com.jp_funda.todomind.extension.getSize
import com.jp_funda.todomind.navigation.RouteGenerator
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.TaskViewModel
import com.jp_funda.todomind.view.components.BackNavigationIcon
import com.jp_funda.todomind.view.components.OgpThumbnail
import com.jp_funda.todomind.view.components.WhiteButton
import com.jp_funda.todomind.view.components.dialog.ColorPickerDialog
import com.jp_funda.todomind.view.components.dialog.ConfirmDialog
import com.jp_funda.todomind.view.components.task_list.TaskListColumn
import com.jp_funda.todomind.view.components.task_list.filterTasksByStatus
import com.jp_funda.todomind.view.mind_map_detail.components.MindMapDetailLoadingContent
import com.jp_funda.todomind.view.mind_map_detail.components.ProgressSection
import com.jp_funda.todomind.view.mind_map_detail.components.thumbnailSection.ThumbnailSection
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MindMapDetailScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    mindMapId: String?,
) {
    val context = LocalContext.current
    val mindMapDetailViewModel = hiltViewModel<MindMapDetailViewModel>()
    val taskViewModel = hiltViewModel<TaskViewModel>()
    val isShowConfirmDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Check whether to edit or create new mind map by mindMapId
        mindMapId?.let { id ->
            delay(Constant.NAV_ANIM_DURATION.toLong())
            mindMapDetailViewModel.loadEditingMindMap(UUID.fromString(id))
        } ?: run { // Create new mind map -> set initial position to horizontal center of mapView
            mindMapDetailViewModel.setEmptyMindMap()
            val mapViewWidth = context.resources.getDimensionPixelSize(R.dimen.map_view_width)
            mindMapDetailViewModel.setX(mapViewWidth.toFloat() / 2 - NodeStyle.HEADLINE_1.getSize().width / 2)
        }

        // Refresh TaskList
        taskViewModel.refreshTaskListData()
    }

    DisposableEffect(key1 = LocalLifecycleOwner.current) {
        onDispose {
            if (mindMapDetailViewModel.isAutoSaveNeeded) {
                mindMapDetailViewModel.saveMindMap()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                        if (mindMapId != null) stringResource(id = R.string.mind_map_detail_editing_title)
                        else stringResource(id = R.string.mind_map_detail_creating_title)
                    )
                },
                backgroundColor = colorResource(id = R.color.deep_purple),
                contentColor = Color.White,
                navigationIcon = { BackNavigationIcon(navController) },
                actions = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(id = R.string.save),
                        )
                    }
                    IconButton(onClick = {
                        // show dialog
                        isShowConfirmDeleteDialog.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                        )
                    }
                }
            )
        },
        backgroundColor = colorResource(id = R.color.deep_purple)
    ) {
        // Confirm Dialog for Deleting mind map
        if (isShowConfirmDeleteDialog.value) {
            ConfirmDialog(
                title = stringResource(id = R.string.question_confirm_delete),
                message = stringResource(id = R.string.notify_tasks_delete),
                isShowDialog = isShowConfirmDeleteDialog,
                isShowNegativeButton = true,
                onClickPositive = {
                    mindMapDetailViewModel.deleteMindMap()
                    navController.popBackStack()
                },
            )
        }

        MindMapDetailContent(navController, mainViewModel)
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MindMapDetailContent(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    val taskViewModel = hiltViewModel<TaskViewModel>()
    val mindMapDetailViewModel = hiltViewModel<MindMapDetailViewModel>()

    val observedTasks by taskViewModel.taskList.observeAsState()
    val selectedTabStatus by taskViewModel.selectedStatusTab.observeAsState(TaskStatus.Open)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show Undo snackbar if currentlyDeletedTask is not null
    LaunchedEffect(snackbarHostState) {
        mainViewModel.currentlyDeletedTask?.let {
            taskViewModel.showUndoDeleteSnackbar(
                snackbarHostState = snackbarHostState,
                deletedTask = it,
            )
        }
        mainViewModel.currentlyDeletedTask = null
    }

    var showingTasks by remember { mutableStateOf(observedTasks) }

    showingTasks = observedTasks?.let { tasks ->
        filterTasksByStatus(
            status = TaskStatus.values().first { it == selectedTabStatus },
            tasks = tasks.filter { task ->
                (task.mindMap?.id == mindMapDetailViewModel.mindMap.value?.id) &&
                        (task.statusEnum == selectedTabStatus)
            },
        )
    }

    // MainUI
    Column {
        TaskListColumn(
            selectedTabStatus = selectedTabStatus,
            onTabChange = { status ->
                taskViewModel.setSelectedStatusTab(status)
            },
            showingTasks = showingTasks,
            onCheckChanged = { task ->
                taskViewModel.updateTaskWithDelay(task)
                scope.launch {
                    taskViewModel.showCheckBoxChangedSnackbar(
                        task,
                        snackbarHostState
                    )
                }
            },
            onRowMove = { fromIndex, toIndex ->
                showingTasks?.let { tasks ->
                    // Replace task's reversedOrder property
                    if (Integer.max(fromIndex, toIndex) < tasks.size) {
                        val fromTask = tasks.sortedBy { task -> task.reversedOrder }
                            .reversed()[fromIndex]
                        val toTask = tasks.sortedBy { task -> task.reversedOrder }
                            .reversed()[toIndex]
                        taskViewModel.replaceReversedOrderOfTasks(fromTask, toTask)
                    }
                }
            },
            onRowClick = { task ->
                navController.navigate(RouteGenerator.TaskDetail(task.id)())
            }
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                MindMapDetailTopContent(navController)
            }
        }
    }

    // Snackbar
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        // Status update Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(bottom = 10.dp),
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MindMapDetailTopContent(navController: NavController) {
    val context = LocalContext.current
    val mindMapDetailViewModel = hiltViewModel<MindMapDetailViewModel>()

    // Set up data
    val observedMindMap by mindMapDetailViewModel.mindMap.observeAsState()
    val ogpResult by mindMapDetailViewModel.ogpResult.observeAsState()

    // Set up TextFields color
    val colors = TextFieldDefaults.textFieldColors(
        textColor = Color.White,
        disabledTextColor = Color.White,
        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = colorResource(id = R.color.teal_200),
    )

    // Set up dialog
    val colorDialogState = rememberMaterialDialogState()
    ColorPickerDialog(colorDialogState) { selectedColor ->
        mindMapDetailViewModel.setColor(selectedColor.toArgb())
    }

    observedMindMap?.let { mindMap ->

        // Launch effect
        LaunchedEffect(ogpResult) {
            if (!mindMap.description.isNullOrEmpty() && mindMapDetailViewModel.isShowOgpThumbnail) {
                mindMapDetailViewModel.extractUrlAndFetchOgp(mindMap.description!!)
            }
        }

        /** Title */
        TextField(
            colors = colors,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .testTag(TestTag.MIND_MAP_DETAIL_TITLE),
            value = mindMap.title ?: "",
            onValueChange = mindMapDetailViewModel::setTitle,
            textStyle = MaterialTheme.typography.h5,
            placeholder = {
                Text(
                    text = "Enter title",
                    color = Color.Gray,
                    style = MaterialTheme.typography.h5,
                )
            }
        )

        /** Thumbnail Section */
        ThumbnailSection(
            mindMapId = mindMap.id,
            isFirstTime = !mindMapDetailViewModel.isEditing,
        ) {
            navigateToMindMapCreate(
                navController = navController,
                mindMap = mindMap,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Date and Edit Mind Map Button Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Date
            val dateString =
                SimpleDateFormat("EEE MM/dd", Locale.getDefault()).format(mindMap.createdDate)
            Text(
                text = "Created on: $dateString",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
            // Edit Mind Map Button
            WhiteButton(
                text = stringResource(id = R.string.mind_map),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_mind_map)
            ) {
                navigateToMindMapCreate(navController, mindMap)
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        /** Color Selector Section */
        TextField(
            colors = colors,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { colorDialogState.show() }
                .testTag(TestTag.MIND_MAP_DETAIL_COLOR),
            value = mindMap.colorHex ?: "",
            onValueChange = {},
            placeholder = {
                Text(text = "Set mind map color", color = Color.Gray)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color_24dp),
                    tint = mindMap.color?.let { Color(it) }
                        ?: run { colorResource(id = R.color.pink_dark) },
                    contentDescription = "Color",
                )
            },
            readOnly = true,
            enabled = false,
        )

        Spacer(modifier = Modifier.height(15.dp))

        /** Description Section */
        TextField(
            colors = colors,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .testTag(TestTag.MIND_MAP_DETAIL_DESCRIPTION),
            value = mindMap.description ?: "",
            onValueChange = {
                mindMapDetailViewModel.setDescription(it)
                // do not check whether description contains url when isShowOgpThumbnail setting is off
                if (mindMapDetailViewModel.isShowOgpThumbnail) {
                    mindMapDetailViewModel.extractUrlAndFetchOgp(it)
                }
            },
            textStyle = MaterialTheme.typography.body1,
            placeholder = {
                Text(text = "Enter description", color = Color.Gray)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notes_24dp),
                    contentDescription = "Description",
                    tint = Color.White
                )
            }
        )

        /** OGP thumbnail */
        ogpResult?.image?.let {
            OgpThumbnail(ogpResult = ogpResult!!, context = context)
        }

        /** Mark as Completed */
        val clickableColors = TextFieldDefaults.textFieldColors(
            disabledTextColor = mindMap.color?.let { Color(it) }
                ?: run { colorResource(id = R.color.pink_dark) },
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = colorResource(id = R.color.teal_200),
        )
        TextField(
            colors = clickableColors,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mindMapDetailViewModel.setIsCompleted(!(mindMap.isCompleted)) }
                .testTag(TestTag.MIND_MAP_DETAIL_IS_COMPLETED),
            value = if (!mindMap.isCompleted) "Mark ${mindMap.title ?: ""} as Completed"
            else "${mindMap.title ?: ""} Completed",
            onValueChange = {},
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id =
                        if (!mindMap.isCompleted) R.drawable.ic_checkbox_unchecked
                        else R.drawable.ic_checkbox_checked
                    ),
                    tint = mindMap.color?.let { Color(it) }
                        ?: run { colorResource(id = R.color.pink_dark) },
                    contentDescription = "mind map status"
                )
            },
            readOnly = true,
            enabled = false,
        )

        /** Progress Section */
        ProgressSection()

        Spacer(modifier = Modifier.height(50.dp))

        /** Task list Section */
        Text(
            text = "Tasks - ${mindMap.title}",
            color = Color.White,
            style = MaterialTheme.typography.h6
        )
    } ?: run {
        MindMapDetailLoadingContent()
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
private fun navigateToMindMapCreate(
    navController: NavController,
    mindMap: MindMap,
) {
    navController.navigate(
        RouteGenerator.MindMapCreate(
            mindMapId = mindMap.id,
            locationX = mindMap.x ?: 0f,
            locationY = mindMap.y ?: 0f,
        )()
    )
}