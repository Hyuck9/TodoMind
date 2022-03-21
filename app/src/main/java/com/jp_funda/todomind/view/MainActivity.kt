package com.jp_funda.todomind.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.Task
import com.jp_funda.todomind.databinding.ActivityMainBinding
import com.jp_funda.todomind.notification.TaskReminder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Set up Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Navigation
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_top -> findViewById<BottomNavigationView>(R.id.nav_view).visibility =
                    View.VISIBLE
                R.id.navigation_task -> findViewById<BottomNavigationView>(R.id.nav_view).visibility =
                    View.VISIBLE
                R.id.navigation_mind_map -> findViewById<BottomNavigationView>(R.id.nav_view).visibility =
                    View.VISIBLE
                R.id.navigation_record -> findViewById<BottomNavigationView>(R.id.nav_view).visibility =
                    View.VISIBLE
                R.id.navigation_settings -> findViewById<BottomNavigationView>(R.id.nav_view).visibility =
                    View.VISIBLE
                else -> findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE
            }
        }

        // todo delete
//        setAlarm(context = applicationContext)
        val task = Task(dueDate = Date())
        TaskReminder.setTaskReminder(task = task, context = applicationContext)
        task.title = "hello"
        TaskReminder.setTaskReminder(task = task, context = applicationContext)
        TaskReminder.cancelTaskReminder(task, applicationContext)
    }

    private fun setAlarm(context: Context) {
        val timeSeconds = System.currentTimeMillis() + 1000
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReminder::class.java)
            .putExtra(TaskReminder.TITLE_KEY, "TITLE")
            .putExtra(TaskReminder.DESC_KEY, "DESCRIPTION")
            .putExtra(TaskReminder.ID_KEY, Task().id.toString())
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeSeconds, pendingIntent)
    }
}