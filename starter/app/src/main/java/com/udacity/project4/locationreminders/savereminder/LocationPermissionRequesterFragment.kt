package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.utils.checkPermissionGranted
import com.udacity.project4.utils.shouldShowPermissionRationale

/**
 * A fragment that encapsulates the logic of foreground location permission requests.
 */
abstract class LocationPermissionRequesterFragment : BaseFragment() {
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Used for logging purposes
        val tag = javaClass.simpleName
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                Log.i(tag, "Precise location permission granted")
                onLocationPermissionsApproved()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                Log.i(tag, "Approximate location permission granted")
                onLocationPermissionsApproved()
            }
            else -> Log.i(tag, "Location permissions denied")
        }
    }
    
    /**
     * Calls [onLocationPermissionsApproved] if location permissions have been granted;
     * otherwise, requests location permissions from the user first.
     */
    protected fun requestLocationPermissionsIfNeeded() {
        when {
            checkPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ->
                // Either one of the two location permissions have been granted.
                onLocationPermissionsApproved()
            
            shouldShowPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ->
                // Show a snackbar to explain why location permissions are needed.
                showLocationPermissionRationale()
            
            else -> launchLocationPermissionRequestDialog()
        }
    }
    
    protected fun launchLocationPermissionRequestDialog() {
        locationPermissionLauncher.launch(locationPermissions)
    }
    
    abstract fun showLocationPermissionRationale()
    
    abstract fun onLocationPermissionsApproved()
    
    private companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}