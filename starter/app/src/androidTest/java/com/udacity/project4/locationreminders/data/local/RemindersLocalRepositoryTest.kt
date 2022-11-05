package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.reflect.KClass

private fun <T : Any> assertInstanceOf(kClass: KClass<T>, actual: Any?) =
    assertThat(actual, instanceOf(kClass.java))

@ExperimentalCoroutinesApi
// Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest : DaoTestProvider() {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val reminder = ReminderDTO("Title", null, "Location", 50.0, 90.0)
    
    private lateinit var repository: RemindersLocalRepository
    
    @Before
    fun initRepository() {
        // Use the main dispatcher for testing
        repository = RemindersLocalRepository(dao, Dispatchers.Main)
    }
    
    @Test
    fun get_reminder_after_saving_successful() = runTest {
        // Save the reminder, then check that the returned result is a success.
        repository.saveReminder(reminder)
        val result = repository.getReminder(reminder.id) as? Result.Success
        assertEquals(reminder, result?.data)
    }
    
    @Test
    fun get_reminder_without_saving_returns_null() = runTest {
        // The repository is empty, so the returned result should be an error.
        assertInstanceOf(Result.Error::class, repository.getReminder(reminder.id))
    }
    
    @Test
    fun get_deleted_reminder_returns_null() = runTest {
        // Save the reminder. We know this step is correct since we already tested it.
        repository.saveReminder(reminder)
        // Delete it, then check that it's `null`.
        repository.deleteReminder(reminder.id)
        assertInstanceOf(Result.Error::class, repository.getReminder(reminder.id))
    }
}