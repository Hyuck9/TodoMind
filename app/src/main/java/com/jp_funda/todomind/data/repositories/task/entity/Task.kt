package com.jp_funda.todomind.data.repositories.task.entity

import android.graphics.Color
import android.util.Log
import com.jp_funda.todomind.data.repositories.mind_map.entity.MindMap
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

enum class TaskStatus(val state: String) {
    InProgress("InProgress"),
    Open("Open"),
    Complete("Complete"),
}

open class Task(
    // General fields
    @PrimaryKey var id: UUID = UUID.randomUUID(),
    var mindMap: MindMap? = null,
    var title: String? = null,
    var description: String? = null,
    var createdDate: Date? = Date(),
    var updatedDate: Date? = Date(),

    // To-Do list fields
    var dueDate: Date? = null,

    // Mind Map fields
    var hierarchy: Int? = null,
    var parentTask: Task? = null,
    var positionNumber: Int? = null,
    var color: Int? = null, // Color Argb int
    statusEnum: TaskStatus? = TaskStatus.InProgress,

    ) : RealmObject() {
    private var status: String = statusEnum?.name ?: TaskStatus.InProgress.name
    var statusEnum: TaskStatus
        get() {
            // default the state to "Open" if the state is unreadable
            return try {
                TaskStatus.valueOf(status)
            } catch (e: IllegalArgumentException) {
                Log.e("Error", e.message ?: "")
                TaskStatus.InProgress
            }
        }
        set(value) {
            // users set state using a FrogState, but it is saved as a string internally
            status = value.state
        }

    // Utils
    fun copy(): Task {
        return Task(
            id,
            mindMap,
            title,
            description,
            createdDate,
            updatedDate,
            dueDate,
            hierarchy,
            parentTask,
            positionNumber,
            color,
            statusEnum,
        )
    }

    companion object {
        val dateFormat = SimpleDateFormat("EEE MM/dd hh:mm aaa", Locale.US)
    }
}