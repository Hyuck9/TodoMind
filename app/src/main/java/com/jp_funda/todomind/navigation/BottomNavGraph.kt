package com.jp_funda.todomind.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.jp_funda.todomind.extension.getLeftSlideInTransaction
import com.jp_funda.todomind.extension.getLeftSlideOutTransaction
import com.jp_funda.todomind.extension.getRightSlideInTransaction
import com.jp_funda.todomind.extension.getRightSlideOutTransaction
import com.jp_funda.todomind.view.MainViewModel
import com.jp_funda.todomind.view.mind_map.MindMapScreen
import com.jp_funda.todomind.view.mind_map_create.MindMapCreateScreen
import com.jp_funda.todomind.view.mind_map_detail.MindMapDetailScreen
import com.jp_funda.todomind.view.record.RecordScreen
import com.jp_funda.todomind.view.settings.SettingsScreen
import com.jp_funda.todomind.view.settings.mind_map_scale.MindMapScaleScreen
import com.jp_funda.todomind.view.settings.oss_licenses.OssLicensesScreen
import com.jp_funda.todomind.view.task.TaskScreen
import com.jp_funda.todomind.view.task_detail.TaskDetailScreen
import com.jp_funda.todomind.view.top.TopScreen

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun BottomNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
) {
    val mainViewModel = hiltViewModel<MainViewModel>()

    AnimatedNavHost(
        navController = navController,
        startDestination = NavigationRoute.Top,
        modifier = modifier,
    ) {
        /** Top Screen. */
        composable(
            route = NavigationRoute.Top,
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = true
            TopScreen(
                navController = navController,
                mainViewModel = mainViewModel,
            )
        }

        /** Task Screen. */
        composable(
            route = NavigationRoute.Task,
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = true
            TaskScreen(
                navController = navController,
                mainViewModel = mainViewModel,
            )
        }

        /** MindMap Screen. */
        composable(
            route = NavigationRoute.MindMap,
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = true
            MindMapScreen(
                navController = navController,
                mainViewModel = mainViewModel,
            )
        }

        /** Record Screen. */
        composable(route = NavigationRoute.Record) {
            bottomBarState.value = true
            RecordScreen()
        }

        /** Settings Screen. */
        composable(
            route = NavigationRoute.Settings,
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = true
            SettingsScreen(navController = navController)
        }

        /** TaskDetail Screen. */
        val taskIdKey = "task id"
        composable(
            route = "${NavigationRoute.TaskDetail}?{$taskIdKey}",
            arguments = listOf(navArgument(taskIdKey) { nullable = true }),
            enterTransition = { getLeftSlideInTransaction() },
            exitTransition = { getLeftSlideOutTransaction() },
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) { backStackEntry ->
            bottomBarState.value = false
            TaskDetailScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                taskId = backStackEntry.arguments?.getString(taskIdKey),
            )
        }

        // Screens - MindMap
        /** MindMapDetail Screen. */
        composable(
            route = NavigationRoute.MindMapDetail,
            enterTransition = { getLeftSlideInTransaction() },
            exitTransition = { getLeftSlideOutTransaction() },
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = false
            MindMapDetailScreen(navController = navController, mainViewModel = mainViewModel)
        }

        /** MindMapCreate Screen. */
        composable(
            route = NavigationRoute.MindMapCreate,
            enterTransition = { getLeftSlideInTransaction() },
            exitTransition = { getLeftSlideOutTransaction() },
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = false
            MindMapCreateScreen(
                navController = navController,
                mainViewModel = mainViewModel,
            )
        }

        // Screens - Settings
        /** MindMapScale Screen. */
        composable(
            route = NavigationRoute.MindMapScale,
            enterTransition = { getLeftSlideInTransaction() },
            exitTransition = { getLeftSlideOutTransaction() },
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = false
            MindMapScaleScreen(navController = navController)
        }

        /** OssLicenses Screen. */
        composable(
            route = NavigationRoute.OssLicenses,
            enterTransition = { getLeftSlideInTransaction() },
            exitTransition = { getLeftSlideOutTransaction() },
            popEnterTransition = { getRightSlideInTransaction() },
            popExitTransition = { getRightSlideOutTransaction() },
        ) {
            bottomBarState.value = false
            OssLicensesScreen(navController = navController)
        }
    }
}