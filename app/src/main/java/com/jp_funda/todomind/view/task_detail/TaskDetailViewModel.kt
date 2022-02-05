package com.jp_funda.todomind.view.task_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jp_funda.todomind.data.repositories.task.TaskRepository
import com.jp_funda.todomind.data.repositories.task.entity.Task
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private var _task = MutableLiveData(Task(createdDate = Date()))
    val task: LiveData<Task> = _task
    var isEditing: Boolean = false

    fun setEditingTask(editingTask: Task) {
        _task.value = editingTask
        isEditing = true
    }

    fun setTitle(title: String) {
        _task.value!!.title = title
        notifyChangeToView()
    }

    fun setDescription(description: String) {
        _task.value!!.description = description
        notifyChangeToView()
    }

    fun setDate(localDate: LocalDate) {
        _task.value!!.dueDate =
            Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        notifyChangeToView()
    }

    fun setTime(localTime: LocalTime) {
        if (_task.value!!.dueDate == null) {
            _task.value!!.dueDate = Date()
        }

        val instant = localTime.atDate(
            _task.value!!.dueDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        ).atZone(ZoneId.systemDefault()).toInstant();
        val date = Date.from(instant)
        _task.value!!.dueDate = date
        notifyChangeToView()
    }

    fun setColor(argb: Int) {
        _task.value!!.color = argb
        notifyChangeToView()
    }

    fun setStatus(statusEnum: TaskStatus) {
        _task.value!!.statusEnum = statusEnum
        notifyChangeToView()
    }

    fun saveTask() {
        // Not editing mode -> Add new task to DB
        // Editing mode -> update task data in DB
        if (!isEditing) {
            repository.createTask(_task.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    Throwable("Error")
                })
        } else {
            repository.updateTask(_task.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    fun deleteTask(task: Task, onSuccess: () -> Unit = {}) {
        if (isEditing) {
            repository.deleteTask(task)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccess() }, {})
        } else {
            onSuccess()
        }
    }

    private fun notifyChangeToView() {
        _task.value = task.value?.copy() ?: Task()
    }
}