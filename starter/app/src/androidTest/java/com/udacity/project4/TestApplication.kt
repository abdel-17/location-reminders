package com.udacity.project4

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.udacity.project4.MyApp.Companion.productionModule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(productionModule, instrumentedTestModule)
        }
    }
    
    companion object {
        val instrumentedTestModule = module {
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