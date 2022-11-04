package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.utils.makeSnackbar
import org.koin.android.ext.android.inject

class SelectLocationFragment : LocationPermissionRequesterFragment(), OnMapReadyCallback {
    
    // Use Koin to get the view model of the SaveReminder
    override val viewModel: SaveReminderViewModel by inject()
    
    private lateinit var binding: FragmentSelectLocationBinding
    
    private lateinit var map: GoogleMap
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var currentMarker: Marker? = null
    
    private var selectedPOI: PointOfInterest? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLocationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.buttonSave.setOnClickListener {
            onSaveClicked()
        }
        
        val mapFragment = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // TODO: add style to the map
        return binding.root
    }
    
    private fun onSaveClicked() {
        // We store a local variable to take advantage of smart casting.
        val poi = selectedPOI
        if (poi == null) {
            makeSnackbar("Please pick a location", Snackbar.LENGTH_LONG).show()
            return
        }
        Log.i(TAG, "Saving... POI: ${poi.name}, position: ${poi.latLng}")
        // Save the location data to the view model.
        viewModel.selectedPOI.value = poi
        // Navigate back to the save reminders fragment.
        findNavController().popBackStack()
    }
    
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        setupOptionsMenu()
        requestLocationPermissionsIfNeeded()
        setupOnMapPoiClick()
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
    
    override fun showLocationPermissionRationale() {
        makeSnackbar(
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Ok") {
            launchLocationPermissionRequestDialog()
        }.show()
    }
    
    // We can safely suppress the missing permission warning because they've already been granted
    @SuppressLint("MissingPermission")
    override fun onLocationPermissionsApproved() {
        // Move the camera to the user's current position
        val request = CurrentLocationRequest.Builder().build()
        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    // Location services are disabled
                    makeSnackbar(
                        R.string.location_required_error,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Done") {
                        onLocationPermissionsApproved()
                    }.show()
                } else {
                    val position = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
                    Log.i(TAG, "Moved map camera to $position")
                }
            }
    }
    
    private fun setupOnMapPoiClick() {
        map.setOnPoiClickListener { poi ->
            selectedPOI = poi
            // POI markers are given a different color to differentiate them from regular markers.
            addMarkerAt(poi.latLng)
        }
    }
    
    private fun addMarkerAt(position: LatLng) {
        Log.i(TAG, "Adding a marker at $position")
        // Replace the old marker
        currentMarker?.remove()
        currentMarker = map.addMarker(MarkerOptions().position(position))
    }
    
    private companion object {
        const val TAG = "SelectLocationFragment"
    }
}
