package com.udacity.project4.locationreminders.savereminder

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.checkPermissionGranted
import com.udacity.project4.utils.makeSnackbar
import com.udacity.project4.utils.shouldShowPermissionRationale
import org.koin.android.ext.android.inject

class SaveReminderFragment : LocationPermissionRequesterFragment() {
    
    // Get the view model this time as a single to be shared with the another fragment
    override val viewModel: SaveReminderViewModel by inject()
    
    private lateinit var binding: FragmentSaveReminderBinding
    
    private lateinit var reminderToBeSaved: ReminderDataItem
    
    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(requireActivity())
    }
    
    private val geofencingPendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        // On Android 11 or higher, the flag needs to be mutable for geofencing to work.
        val intentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        PendingIntent.getBroadcast(requireContext(), 0, intent, intentFlags)
    }
    
    private val backgroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i(TAG, "Background location permission granted")
            addGeofence()
        } else {
            Log.i(TAG, "Background location permission denied")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveReminderBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.selectLocation.setOnClickListener {
            findNavController().navigate(SaveReminderFragmentDirections.actionToSelectLocation())
        }
        binding.saveReminder.setOnClickListener {
            viewModel.getReminderIfValid()?.let { reminder ->
                reminderToBeSaved = reminder
                // Request location permissions to add a geofence request
                requestLocationPermissionsIfNeeded()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Make sure to clear the view model after this fragment as it's a shared singleton.
        Log.i(TAG, "Clearing view model")
        viewModel.onClear()
    }
    
    override fun showLocationPermissionRationale() {
        makeSnackbar(
            "Background location access is needed to track reminders.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Ok") {
            launchLocationPermissionRequestDialog()
        }.show()
    }
    
    override fun onLocationPermissionsApproved() {
        when {
            // On android versions below Q, background location can be access once foreground
            // location permissions are granted.
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                    checkPermissionGranted(ACCESS_BACKGROUND_LOCATION) -> addGeofence()
            
            shouldShowPermissionRationale(ACCESS_BACKGROUND_LOCATION) ->
                showBackgroundLocationPermissionRationale()
            
            else -> backgroundLocationPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationPermissionRationale() {
        makeSnackbar(
            "Background location access is required to track reminders",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Ok") {
            backgroundLocationPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
        }.show()
    }
    
    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        // Add a geofencing request.
        val geofence = Geofence.Builder()
            // Set the id of the geofence
            .setRequestId(reminderToBeSaved.id)
            .setCircularRegion(
                reminderToBeSaved.latitude,
                reminderToBeSaved.longitude,
                GEOFENCE_RADIUS
            )
            // Observe when the user enters a geofence
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(GEOFENCE_EXPIRATION_DURATION_MILLIS)
            .build()
        val geofencingRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            // Trigger the `GEOFENCE_TRANSITION_ENTER` event if we're already in the geofence
            // when the request is created.
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
        geofencingClient.addGeofences(geofencingRequest, geofencingPendingIntent)
            .addOnSuccessListener {
                Log.i(TAG, "Geofence added successfully")
                // Save the reminder to the database and navigate back
                // to the reminders list fragment.
                viewModel.saveReminder(reminderToBeSaved)
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                // Tell the user that it failed.
                makeSnackbar(
                    "Failed to add the reminder geofence",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }
    
    private companion object {
        const val TAG = "SaveReminderFragment"
        
        const val GEOFENCE_RADIUS = 100f
        
        // 1 hour
        const val GEOFENCE_EXPIRATION_DURATION_MILLIS = 1000L * 60L * 60L
    }
}
