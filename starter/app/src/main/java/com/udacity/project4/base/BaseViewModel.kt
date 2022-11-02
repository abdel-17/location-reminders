package com.udacity.project4.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.utils.SingleLiveEvent

/**
 * A base view model class containing common live data objects.
 */
@Suppress("PropertyName")
abstract class BaseViewModel(protected val app: Application) : AndroidViewModel(app) {
    
    protected val _showLoading = MutableLiveData<Boolean>()
    
    /**
     * Yhe view should show a loading indicator when the value of this live data is `true`.
     */
    val showLoading: LiveData<Boolean>
        get() = _showLoading
    
    protected val _showSnackbar = SingleLiveEvent<String?>()
    
    /**
     * The view should show a snackbar when the value of this live data
     * is a non-null message.
     */
    val showSnackbar: LiveData<String?>
        get() = _showSnackbar
}