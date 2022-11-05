package com.udacity.project4.util

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.After
import org.junit.Before
import org.junit.rules.TestWatcher

/**
 * Registers [idlingResource] before each test runs and unregisters it after it's done.
 */
class IdlingResourceRule(private val idlingResource: IdlingResource) : TestWatcher() {
    
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(idlingResource)
    }
    
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}