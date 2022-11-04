package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.util.MainDispatcherRule
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Replace the main dispatcher with a test dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: SaveReminderViewModel
    
    @Before
    fun createViewModel() {
        // Test the view model with a fake data source because this is a unit test.
        // A repository would require a UI test.
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )
    }
    
    @After
    fun stopKoinApp() {
        stopKoin()
    }
    
    private val reminder = ReminderDataItem("Title", null, "Location", 50.0, 90.0)
    
    @Test
    fun `Loading indicator is shown while saving data`() = runTest {
        viewModel.saveReminder(reminder)
        assertEquals(true, viewModel.showLoading.getOrAwaitValue())
        // Execute the launched coroutine. After it finishes, the loading indicator should be hidden.
        advanceUntilIdle()
        assertEquals(false, viewModel.showLoading.getOrAwaitValue())
    }
    
    @Test
    fun `Save button is hidden while saving data`() = runTest {
        viewModel.saveReminder(reminder)
        assertEquals(false, viewModel.showSaveButton.getOrAwaitValue())
        // Execute the launched coroutine. After it finishes, the save button should appear again.
        advanceUntilIdle()
        assertEquals(true, viewModel.showSaveButton.getOrAwaitValue())
    }
}