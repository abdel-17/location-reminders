package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.utils.checkPermissionGranted
import com.udacity.project4.utils.makeSnackbar
import com.udacity.project4.utils.shouldShowPermissionRationale
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {
    
    // Use Koin to get the view model of the SaveReminder
    override val viewModel: SaveReminderViewModel by inject()
    
    private lateinit var binding: FragmentSelectLocationBinding
    
    private lateinit var map: GoogleMap
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                Log.i(TAG, "Precise location permission granted")
                moveToCurrentLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                Log.i(TAG, "Approximate location permission granted")
                moveToCurrentLocation()
            }
            else -> {
                Log.i(TAG, "Location permissions denied")
                // Tell the user to cooperate with us a little. I swear we're not trying to
                // spy on you or anything.
                makeSnackbar(
                    "Please enable location permissions from the app's settings",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Done") {
                    // Try again
                    moveToCurrentLocation()
                }.show()
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLocationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.buttonSave.setOnClickListener {
            onSavedClicked()
        }
        
        val mapFragment = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // TODO: zoom to the user location after taking his permission
        // TODO: add style to the map
        // TODO: put a marker to location that the user selected
        // TODO: call this function after the user confirms on the selected location
        return binding.root
    }
    
    private fun onSavedClicked() {
        // TODO setup save action
    }
    
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        setupOptionsMenu()
        requestPermissionsThenMoveToCurrentLocation()
        onLocationSelected()
    }
    
    /**
     * Requests location permissions and moves to the current location (if granted).
     */
    private fun requestPermissionsThenMoveToCurrentLocation() {
        when {
            LOCATION_PERMISSIONS.any { checkPermissionGranted(it) } -> {
                // Either one of the two location permissions have been granted.
                moveToCurrentLocation()
            }
            LOCATION_PERMISSIONS.any { shouldShowPermissionRationale(it) } -> {
                // Show a snackbar to explain why location permissions are needed.
                makeSnackbar(
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Ok") {
                    // Request the permissions
                    requestLocationPermissions()
                }.show()
            }
            else -> requestLocationPermissions()
        }
    }
    
    /**
     * Requests foreground location permissions.
     */
    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(LOCATION_PERMISSIONS.toTypedArray())
    }
    
    // We can suppress the missing permission warning because we requested it earlier.
    @SuppressLint("MissingPermission")
    private fun moveToCurrentLocation() {
        val request = CurrentLocationRequest.Builder().build()
        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    makeSnackbar(
                        "Please enable location services",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Done") {
                        moveToCurrentLocation()
                    }.show()
                } else {
                    val position = LatLng(location.latitude, location.longitude)
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(position, 15f)
                    )
                }
            }
    }
    
    private fun setupOptionsMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_options, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                map.mapType = when (menuItem.itemId) {
                    R.id.normal_map -> GoogleMap.MAP_TYPE_NORMAL
                    R.id.hybrid_map -> GoogleMap.MAP_TYPE_HYBRID
                    R.id.satellite_map -> GoogleMap.MAP_TYPE_SATELLITE
                    R.id.terrain_map -> GoogleMap.MAP_TYPE_TERRAIN
                    else -> return false
                }
                return true
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
    }
    
    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
    }
    
    private companion object {
        const val TAG = "SelectLocationFragment"
        
        val LOCATION_PERMISSIONS = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
