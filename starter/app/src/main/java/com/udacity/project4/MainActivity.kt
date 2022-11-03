package com.udacity.project4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

// The starting point of our app. Only responsible for setting the layout
// containing a NavHostFragment that handles app navigation.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}