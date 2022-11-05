package com.udacity.project4

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import org.koin.dsl.module

class TestApplication : Application() {
    
    companion object {
        val fakeRepositoryModule = module {
            single { FakeDataSource() }
            // Override the data source with a fake repository for testing
            single<ReminderDataSource> { get<FakeDataSource>() }
        }
    }
}

@Suppress("unused")
class InstrumentationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}