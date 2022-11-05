package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.udacity.project4.MyApp.Companion.productionModule
import com.udacity.project4.R
import com.udacity.project4.TestApplication.Companion.fakeRepositoryModule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.IdlingResourceRule
import com.udacity.project4.util.KoinTestModulesRule
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
// UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    
    @get:Rule
    val dataBindingIdlingResourceRule = IdlingResourceRule(dataBindingIdlingResource)
    
    private val reminder = ReminderDTO("Title", null, "Location", 50.0, 90.0)
    
    private val dataSource: FakeDataSource by inject()
    
    // Replace the repository with a fake repository
    @get:Rule
    val koinTestModulesRule = KoinTestModulesRule(productionModule, fakeRepositoryModule)
    
    @After
    fun resetDataSource() = runBlocking {
        // Start the next test fresh.
        dataSource.deleteAllReminders()
    }
    
    private fun launchFragmentInContainer() = launchFragmentInContainer<ReminderListFragment>(
        themeResId = R.style.AppTheme
    ).also {
        // Needed because we're using data binding
        dataBindingIdlingResource.monitorFragment(it)
    }
    
    @Test
    fun clicking_on_FAB_navigates_to_SaveReminderFragment() {
        // Mock the nav controller to verify that clicking the fab navigates correctly.
        val navController = mock(NavController::class.java)
        launchFragmentInContainer().onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        // When we click on the fab, verify that we navigate to SaveReminderFragment.
        onView(withId(R.id.addReminderFAB))
            .perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.actionToSaveReminder())
    }
    
    @Test
    fun empty_list_shows_empty_indicator() = runTest {
        launchFragmentInContainer()
        onView(withId(R.id.noDataTextView))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun reminders_list_data_are_shown() = runTest {
        dataSource.saveReminder(reminder)
        launchFragmentInContainer()
        onView(withId(R.id.title))
            .check(matches(withText(reminder.title)))
    }
    
    @Test
    fun snackbar_shown_when_loading_error_occurs() = runTest {
        dataSource.shouldReturnError = true
        try {
            launchFragmentInContainer()
            // Check that a snackbar is shown
            val snackbarTextId = com.google.android.material.R.id.snackbar_text
            onView(withId(snackbarTextId))
                .check(matches(withText(FakeDataSource.UNKNOWN_ERROR_MESSAGE)))
        } finally {
            dataSource.shouldReturnError = false
        }
    }
}