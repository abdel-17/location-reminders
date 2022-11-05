package com.udacity.project4.util

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

/**
 * Starts Koin with the given modules before each tests and stops afterwards.
 */
class KoinTestModulesRule(vararg modules: Module) : TestWatcher() {
    private val modules = modules.toList()
    
    override fun starting(description: Description) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        startKoin {
            androidContext(context)
            modules(modules)
        }
    }
    
    override fun finished(description: Description) {
        stopKoin()
    }
}