package com.udacity.project4.locationreminders.reminderslist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class RemindersListAdapter(
    private val onItemClick: (selectedReminder: ReminderDataItem) -> Unit
) : ListAdapter<ReminderDataItem, ReminderListViewHolder>(ReminderItemDiffUtilCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListViewHolder {
        return ReminderListViewHolder.fromParent(parent)
    }
    
    override fun onBindViewHolder(holder: ReminderListViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }
    
    private object ReminderItemDiffUtilCallback : DiffUtil.ItemCallback<ReminderDataItem>() {
        override fun areItemsTheSame(oldItem: ReminderDataItem, newItem: ReminderDataItem): Boolean {
            return oldItem.id == newItem.id
        }
    
        override fun areContentsTheSame(oldItem: ReminderDataItem, newItem: ReminderDataItem): Boolean {
            return oldItem == newItem
        }
    }
}