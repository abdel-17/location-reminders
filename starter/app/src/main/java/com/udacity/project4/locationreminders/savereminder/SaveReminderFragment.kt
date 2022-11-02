package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    // Get the view model this time as a single to be shared with the another fragment
    override val viewModel: SaveReminderViewModel by inject()
    
    private lateinit var binding: FragmentSaveReminderBinding
    
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
        viewModel.doneSaving.observe(viewLifecycleOwner) {
            onDoneSaving()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.selectLocation.setOnClickListener {
            // TODO Navigate to another fragment to get the user location
        }
        binding.saveReminder.setOnClickListener {
            val reminderDataItem = viewModel.reminderDataItem
            viewModel.validateAndSaveReminder(reminderDataItem)
            // TODO add a geofencing request
        }
    }
    
    private fun onDoneSaving() {
        Log.i(TAG, "Done saving")
        // Navigate back when we're done saving the reminder to the database
        findNavController().popBackStack()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Make sure to clear the view model after this fragment as it's a shared singleton.
        Log.i(TAG, "Clearing view model")
        viewModel.onClear()
    }
    
    private companion object {
        const val TAG = "SaveReminderFragment"
    }
}
