package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.runner.RunWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {
    
    private val sampleReminder = ReminderDTO("Title", null, "Location", 50.0, 90.0)
    
    private lateinit var database: RemindersDatabase
    
    private lateinit var dao: RemindersDao
    
    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.remindersDao
    }
    
    @After
    fun closeDatabase() {
        database.close()
    }
    
    @Test
    fun get_reminder_after_saving_successful() = runTest {
        // Save the reminder then check that it is in the database.
        dao.saveReminder(sampleReminder)
        assertNotNull(dao.getReminderById(sampleReminder.id))
    }
    
    @Test
    fun get_reminder_without_saving_returns_null() = runTest {
        // The database is empty. It shouldn't contain any reminders.
        assertNull(dao.getReminderById(sampleReminder.id))
    }
    
    @Test
    fun get_deleted_reminder_returns_null() = runTest {
        // Save the reminder. We know this step is correct since we already tested it.
        dao.saveReminder(sampleReminder)
        // Delete it, then check that it's `null`.
        dao.deleteReminderById(sampleReminder.id)
        assertNull(dao.getReminderById(sampleReminder.id))
    }
}