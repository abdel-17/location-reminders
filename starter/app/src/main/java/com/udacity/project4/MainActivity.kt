package com.udacity.project4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

// The starting point of our app. Only responsible for setting the layout
// containing a NavHostFragment that handles app navigation.
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarForNavigation()
    }
    
    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
    
    private fun setupActionBarForNavigation() {
        // Hide the up button in the login screen as well as the home screen.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.reminderListFragment, R.id.authenticationFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}