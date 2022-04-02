package com.jp_funda.todomind.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.jp_funda.todomind.BuildConfig
import com.jp_funda.todomind.R
import com.jp_funda.todomind.view.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// TODO add dismiss ogp tag setting
@ExperimentalPagerApi
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Settings") },
                            backgroundColor = colorResource(id = R.color.deep_purple),
                            contentColor = Color.White,
                        )
                    },
                    backgroundColor = colorResource(id = R.color.deep_purple)
                ) {
                    SettingsContent()
                }
            }
        }
    }

    @Composable
    fun SettingsContent() {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            /** Personal Settings */
            SettingsGroup(title = "Personal Settings") {
                // Mind Map Scale
                SettingRowWithNext(
                    icon = Icons.Default.LocationOn,
                    title = "Default Mind Map Scale",
                    selectedValue = "${(viewModel.defaultMindMapScale * 100).roundToInt()} %"
                ) { findNavController().navigate(R.id.action_navigation_settings_to_navigation_mind_map_scale) }

                Divider(color = colorResource(id = R.color.white_1))

                // OGP thumbnail setting
                SettingRowWithSwitch(
                    painter = painterResource(id = R.drawable.ic_link_24),
                    title = "Show Link Thumbnail",
                    initialValue = viewModel.isShowOgpThumbnail,
                ) {
                    viewModel.setIsShowOgpThumbnail(it)
                }
            }

            /** About TodoMind */
            SettingsGroup("About TodoMind") {
                // APP version
                SettingRowOnlyText(
                    icon = Icons.Default.Build,
                    title = "App version",
                    value = BuildConfig.VERSION_NAME
                )

                Divider(color = colorResource(id = R.color.white_1))

                // OSS Licenses
                SettingRowWithNext(
                    icon = Icons.Default.List,
                    title = "Open source licenses"
                ) { findNavController().navigate(R.id.action_navigation_settings_to_navigation_oss_licenses) }

                Divider(color = colorResource(id = R.color.white_1))

                // Intro
                SettingRowOnlyText(
                    icon = Icons.Default.Info,
                    title = "Watch Introduction",
                    value = "",
                ) {
                    startActivity(Intent(requireContext(), IntroActivity::class.java))
                }
            }

            /** Notifications */
            SettingsGroup("Notifications") {
                SettingRowWithSwitch(
                    icon = Icons.Default.Notifications,
                    title = "Remind task deadline",
                    initialValue = viewModel.isRemindTaskDeadline,
                ) {
                    viewModel.setIsRemindTaskDeadline(it)
                }
            }

            /** Under Construction */
            SettingsGroup("Under Development") {
                SettingRowComingSoon(
                    painter = painterResource(id = R.drawable.ic_backup_24),
                    title = "Cloud Backup",
                    subTitle = "coming soon...",
                ) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "This feature is under construction",
                            actionLabel = "OK"
                        )
                    }
                }
            }

            // Copy Light
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mind Map task management app",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.subtitle2.fontSize * 0.8,
                    modifier = Modifier.alpha(0.6f)
                )

                Divider(
                    color = colorResource(id = R.color.white_1),
                    modifier = Modifier
                        .padding(10.dp)
                        .width(200.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorResource(id = R.color.light_purple))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mind_map),
                        contentDescription = "App icon",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(60.dp)
                            .alpha(0.6f),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "© 2022 by Masato Ishikawa",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.subtitle2.fontSize * 0.8,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

        // Snackbar
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Status update Snackbar
            SnackbarHost(hostState = snackbarHostState)
        }
    }

    @Composable
    fun SettingsGroup(
        title: String,
        content: @Composable () -> Unit,
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2,
                color = Color.LightGray,
                modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
            )
            Surface(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                color = colorResource(id = R.color.steel_dark)
            ) {
                Column { content() }
            }
        }
    }

    /** SettingRow with only text info */
    @Composable
    fun SettingRowOnlyText(
        icon: ImageVector? = null,
        painter: Painter? = null,
        title: String,
        value: String,
        onClick: () -> Unit = {},
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            painter?.let {
                Icon(
                    painter = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(Modifier.weight(1f))
            Text(text = value, color = Color.White)
        }
    }

    /** SettingRow with next screen */
    @Composable
    fun SettingRowWithNext(
        icon: ImageVector,
        title: String,
        selectedValue: String? = null,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                tint = colorResource(id = R.color.grey),
                contentDescription = "Title",
                modifier = Modifier.height(40.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(Modifier.weight(1f))
            selectedValue?.let { Text(text = it, color = Color.White) }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                tint = colorResource(id = R.color.grey),
                contentDescription = "Next"
            )
        }
    }

    /** SettingRow with switch */
    @Composable
    fun SettingRowWithSwitch(
        icon: ImageVector? = null,
        painter: Painter? = null,
        title: String,
        initialValue: Boolean,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        val checkedState = remember { mutableStateOf(initialValue) }

        Row(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            painter?.let {
                Icon(
                    painter = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onCheckedChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(id = R.color.teal_200),
                    checkedTrackColor = colorResource(id = R.color.teal_200),
                    checkedTrackAlpha = 0.8f,
                )
            )
        }
    }

    @Composable
    fun SettingRowComingSoon(
        icon: ImageVector? = null,
        painter: Painter? = null,
        title: String,
        subTitle: String,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            painter?.let {
                Icon(
                    painter = it,
                    tint = colorResource(id = R.color.grey),
                    contentDescription = "Title",
                    modifier = Modifier.height(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = title,
                color = Color.DarkGray,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(Modifier.weight(1f))
            Text(text = subTitle, color = Color.White)
        }
    }
}

