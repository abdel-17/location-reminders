package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RemindersListViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : AndroidViewModel(app) {
    private val _reminders =  MutableLiveData<List<ReminderDataItem>>()
    
    // A list that holds the reminder data to be displayed on the UI
    val reminders: LiveData<List<ReminderDataItem>?>
        get() = _reminders
    
    /**
     *  The value of this live data is `true` if the value of
     *  [reminders] is either `null` or an empty list.
     */
    val isEmpty = _reminders.map { remindersList ->
        remindersList.isNullOrEmpty()
    }
    
    private val _islLoading = MutableLiveData<Boolean>()
    
    val isLoading: LiveData<Boolean>
        get() = _islLoading
    
    private val _errorMessage = SingleLiveEvent<String?>()
    
    val errorMessage: LiveData<String?>
        get() = _errorMessage
    
    /**
     * Get all the reminders from the data source and add them to [reminders]
     * to be shown on the UI, or show an error (if any).
     */
    fun loadReminders() {
        viewModelScope.launch {
            _islLoading.postValue(true)
            // Interacting with the data source has to be through a coroutine
            val result = dataSource.getReminders()
            _islLoading.postValue(false)
            when (result) {
                is Result.Success -> {
                    val remindersList = result.data.map(ReminderDTO::toReminderDataItem)
                    _reminders.postValue(remindersList)
                }
                is Result.Error -> {
                    // `_errorMessage` is a custom live data implementation whose setter runs on
                    // the main thread, so we don't need to call `postValue`.
                    _errorMessage.value = result.message
                }
            }
        }
    }
}