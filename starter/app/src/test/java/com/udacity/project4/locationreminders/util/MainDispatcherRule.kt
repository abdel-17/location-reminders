package com.udacity.project4.locationreminders.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces the main dispatcher before each test with the given test dispatcher,
 * then resets it back after the test is done.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
): TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        // Replaces the main dispatcher with our custom test dispatcher.
        // This is needed because we are running a local unit test, and
        // using the main dispatcher requires the android main looper.
        Dispatchers.setMain(dispatcher)
    }
    
    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}