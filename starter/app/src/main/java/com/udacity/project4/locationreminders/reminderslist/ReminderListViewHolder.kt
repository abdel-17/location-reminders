package com.udacity.project4.locationreminders.reminderslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.databinding.ListItemReminderBinding

class ReminderListViewHolder private constructor(
    private val binding: ListItemReminderBinding
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(item: ReminderDataItem, onClick: (ReminderDataItem) -> Unit) {
        binding.item = item
        binding.root.setOnClickListener { onClick(item) }
    }
    
    companion object {
        fun fromParent(parent: ViewGroup): ReminderListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemReminderBinding.inflate(inflater, parent, false)
            return ReminderListViewHolder(binding)
        }
    }
}