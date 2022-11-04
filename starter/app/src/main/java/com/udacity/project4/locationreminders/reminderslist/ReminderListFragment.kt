package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    // Use Koin to retrieve the ViewModel instance
    override val viewModel: RemindersListViewModel by viewModel()
    
    private lateinit var binding: FragmentRemindersBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
        setupRecyclerView()
        setupOptionsMenu()
        // Navigate to the login fragment if the user is signed out.
        if (FirebaseAuth.getInstance().currentUser == null) {
            navigateToLogin()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Load the reminders list on the ui
        viewModel.loadReminders()
    }
    
    private fun navigateToAddReminder() {
        Log.i(TAG, "Navigating to the add reminder screen")
        findNavController().navigate(ReminderListFragmentDirections.actionToSaveReminder())
    }
    
    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter { item ->
            Log.i(TAG, "Reminder list item clicked with id ${item.id}")
            launchRemindersDescriptionActivityFor(item)
        }
        binding.remindersRecyclerView.adapter = adapter
    }
    
    private fun launchRemindersDescriptionActivityFor(reminderDataItem: ReminderDataItem) {
        // Create an intent to launch the activity
        val intent = ReminderDescriptionActivity.newIntent(requireContext(), reminderDataItem)
        startActivity(intent)
    }
    
    private fun navigateToLogin() {
        Log.i(TAG, "Navigating to the login screen")
        findNavController().navigate(ReminderListFragmentDirections.actionToLogin())
    }
    
    private fun setupOptionsMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Display a logout menu item
                menuInflater.inflate(R.menu.main_menu, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        signOut()
                        true
                    }
                    else -> false
                }
            }
        }
        // Add a menu that's shown when the lifecycle of the view is at least `STARTED`.
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
    }
    
    private fun signOut() {
        Log.i(TAG, "Logging out")
        AuthUI.getInstance().signOut(requireContext())
        navigateToLogin()
    }
    
    private companion object {
        const val TAG = "ReminderListFragment"
    }
}
