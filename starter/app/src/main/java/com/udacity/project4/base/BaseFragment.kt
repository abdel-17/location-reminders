package com.udacity.project4.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.utils.makeSnackbar

abstract class BaseFragment : Fragment() {
    
    abstract val viewModel: BaseViewModel
    
    protected open val snackbarDuration = Snackbar.LENGTH_LONG
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show a snackbar when the view model emits a non-null snackbar event.
        viewModel.showSnackbar.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                makeSnackbar(message, snackbarDuration).show()
            }
        }
    }
}