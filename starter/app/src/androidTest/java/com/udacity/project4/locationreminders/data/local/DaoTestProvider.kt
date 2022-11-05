package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before

/**
 * Initializes a new in-memory database before each test
 * and closes it afterwards.
 */
open class DaoTestProvider {
    private lateinit var database: RemindersDatabase
    
    lateinit var dao: RemindersDao
    
    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.remindersDao
    }
    
    @After
    fun closeDatabase() {
        database.close()
    }
}