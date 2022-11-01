package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
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
        binding.apply {
            viewModel = this@ReminderListFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            // TODO use data binding to setup the refresh listener
            refreshLayout.setOnRefreshListener {
                this@ReminderListFragment.viewModel.loadReminders()
            }
            addReminderFAB.setOnClickListener {
                navigateToAddReminder()
            }
        }
        setupRecyclerView()
        setDisplayHomeAsUpEnabled(false)
        setupOptionsMenu()
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
    
    private fun setupOptionsMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Display a logout menu item
                menuInflater.inflate(R.menu.main_menu, menu)
            }
    
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        // TODO: add the logout implementation
                        true
                    }
                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }
}
