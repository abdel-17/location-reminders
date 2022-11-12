package com.udacity.project4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.utils.NOTIFICATION_CHANNEL_ID

// The starting point of our app. Only responsible for setting the layout
// containing a NavHostFragment that handles app navigation.
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // On Android O or later, sending notifications requires creating a channel.
        createNotificationChannel()
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        // Navigate to the login screen if no user is logged in
        if (FirebaseAuth.getInstance().currentUser == null) {
            navigateToLogin(navController = navHostFragment.navController)
        }
    }
    
    private fun navigateToLogin(navController: NavController) {
        Log.i("MainActivity", "Navigating to the login screen")
        navController.navigate(ReminderListFragmentDirections.actionToLogin())
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("MainActivity", "Creating the notification channel")
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}