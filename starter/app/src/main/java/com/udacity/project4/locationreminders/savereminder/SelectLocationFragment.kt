package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {
    
    // Use Koin to get the view model of the SaveReminder
    override val viewModel: SaveReminderViewModel by inject()
    
    private lateinit var binding: FragmentSelectLocationBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLocationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mapFragment = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // TODO: zoom to the user location after taking his permission
        // TODO: add style to the map
        // TODO: put a marker to location that the user selected
        // TODO: call this function after the user confirms on the selected location
        return binding.root
    }
    
    override fun onMapReady(map: GoogleMap) {
        val sydney = LatLng(34.0, 151.0)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        setupOptionsMenu(map)
        onLocationSelected(map)
    }
    
    private fun setupOptionsMenu(map: GoogleMap) {
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
    
    private fun onLocationSelected(map: GoogleMap) {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
    }
}
