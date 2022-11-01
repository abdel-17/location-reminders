package com.udacity.project4.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListAdapter

/**
 * Use binding adapter to set the recycler view data
 */
@BindingAdapter("reminderList")
fun RecyclerView.setReminderList(items: List<ReminderDataItem>?) {
    val adapter = this.adapter as RemindersListAdapter
    adapter.submitList(items)
}

/**
 * Use this binding adapter to show and hide the views using boolean variables
 */
@BindingAdapter("fadeVisibleIf")
fun View.setVisibleIf(visible: Boolean) {
    if (tag == null) {
        tag = true
        visibility = if (visible) View.VISIBLE else View.GONE
    } else {
        this.animate().cancel()
        if (visible && visibility == View.GONE) {
            this.fadeIn()
        } else if (!visible && visibility == View.VISIBLE) {
            this.fadeOut()
        }
    }
}