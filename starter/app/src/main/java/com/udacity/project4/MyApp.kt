package com.udacity.project4

import android.app.Application
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@MyApp)
            modules(productionModule)
        }
    }
    
    companion object {
        // use Koin Library as a service locator
        val productionModule = module {
            // Declare a ViewModel - be later inject into Fragment with dedicated injector
            // using by viewModel()
            viewModel {
                RemindersListViewModel(get(), get())
            }
            // Declare singleton definitions to be later injected using by inject()
            single {
                // This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(get(), get())
            }
            single<ReminderDataSource> { RemindersLocalRepository(get()) }
            single { RemindersDatabase.getInstance(androidContext()).remindersDao }
        }
    }
}