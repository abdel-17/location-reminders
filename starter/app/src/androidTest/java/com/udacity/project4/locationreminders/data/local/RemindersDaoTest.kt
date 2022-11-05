package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class RemindersDaoTest : DaoTestProvider() {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val reminder = ReminderDTO("Title", null, "Location", 50.0, 90.0)
    
    @Test
    fun get_reminder_after_saving_successful() = runTest {
        // Save the reminder, then check that it is in the database.
        dao.saveReminder(reminder)
        assertEquals(reminder, dao.getReminderById(reminder.id))
    }
    
    @Test
    fun get_reminder_without_saving_returns_null() = runTest {
        // The database is empty. It shouldn't contain any reminders.
        assertNull(dao.getReminderById(reminder.id))
    }
    
    @Test
    fun get_deleted_reminder_returns_null() = runTest {
        // Save the reminder. We know this step is correct since we already tested it.
        dao.saveReminder(reminder)
        // Delete it, then check that it's `null`.
        dao.deleteReminderById(reminder.id)
        assertNull(dao.getReminderById(reminder.id))
    }
}