package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [ReminderDTO::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {
    abstract val remindersDao: RemindersDao
    
    companion object {
        @Volatile
        private var INSTANCE: RemindersDatabase? = null
        
        fun getInstance(context: Context): RemindersDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RemindersDatabase::class.java,
                    "locationReminders.db"
                ).build()
            }
        }
    }
}