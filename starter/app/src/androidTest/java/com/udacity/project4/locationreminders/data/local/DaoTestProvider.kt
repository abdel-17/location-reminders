package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Initializes a new in-memory database before each test
 * and closes it afterwards.
 */
open class DaoTestProvider {
    private lateinit var database: RemindersDatabase
    
    lateinit var dao: RemindersDao
    
    // Execute tasks synchronously instead of in the background.
    // This is needed for testing Architecture components.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
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
}