package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : Fragment() {
    // Use Koin to retrieve the ViewModel instance
    val viewModel: RemindersListViewModel by viewModel()
    
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
        setDisplayHomeAsUpEnabled(false)
        setupOptionsMenu()
        navigateToLoginUnlessAuthenticated()
    }
    
    override fun onResume() {
        super.onResume()
        // Load the reminders list on the ui
        viewModel.loadReminders()
    }
    
    private fun navigateToAddReminder() {
        // TODO add navigation logic
    }
    
    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter { item ->
            // TODO setup on click action
        }
        binding.remindersRecyclerView.adapter = adapter
    }
    
    private fun navigateToLoginUnlessAuthenticated() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Navigate to the authentication fragment
            Log.i(TAG, "Navigating to the login screen")
            findNavController().navigate(ReminderListFragmentDirections.actionToLogin())
        }
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
                        Log.i(TAG, "Logging out")
                        AuthUI.getInstance().signOut(requireContext())
                        true
                    }
                    else -> false
                }
            }
        }
        // Add a menu that's shown when the lifecycle of the view is at least `STARTED`.
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
    }
    
    private companion object {
        const val TAG = "ReminderListFragment"
    }
}
