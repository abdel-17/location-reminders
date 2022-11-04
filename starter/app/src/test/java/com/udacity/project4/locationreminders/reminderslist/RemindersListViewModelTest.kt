package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Replace the main dispatcher with a test dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var fakeDataSource: FakeDataSource
    
    private lateinit var viewModel: RemindersListViewModel
    
    private val reminder = ReminderDTO("Title", null, "Location", 50.0, 90.0)
    
    @Before
    fun createViewModel() {
        // Test the view model with a fake data source because this is a unit test.
        // A repository would require a UI test.
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }
    
    @After
    fun stopKoinApp() {
        stopKoin()
    }
    
    @Test
    fun `Loading indicator is shown while loading data`() = runTest {
        viewModel.loadReminders()
        assertEquals(true, viewModel.showLoading.getOrAwaitValue())
        // Execute the launched coroutine. After it finishes, the loading indicator should be hidden.
        advanceUntilIdle()
        assertEquals(false, viewModel.showLoading.getOrAwaitValue())
    }
    
    @Test
    fun `Empty list indicator is hidden while loading data`() = runTest {
        viewModel.loadReminders()
        assertEquals(false, viewModel.showEmpty.getOrAwaitValue())
        // Execute the launched coroutine. After it finishes, the empty list indicator should be
        // shown again because the fake data source is empty.
        advanceUntilIdle()
        assertEquals(true, viewModel.showEmpty.getOrAwaitValue())
    }
    
    @Test
    fun `Empty list indicator is hidden after loading non-empty data`() = runTest {
        fakeDataSource.saveReminder(reminder)
        viewModel.loadReminders()
        // Execute the launched coroutine. After it finishes, the empty list indicator should be
        // hidden because a reminder has been loaded.
        advanceUntilIdle()
        assertEquals(false, viewModel.showEmpty.getOrAwaitValue())
    }
    
    @Test
    fun `Snackbar is shown when loading fails`() = runTest {
        fakeDataSource.shouldReturnError = true
        viewModel.loadReminders()
        // Execute the launched coroutine. After it finishes, `showSnackbar` should have
        // a non-null value.
        advanceUntilIdle()
        assertNotNull(viewModel.showSnackbar.getOrAwaitValue())
    }
}