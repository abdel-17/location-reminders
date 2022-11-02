package com.udacity.project4.authentication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.databinding.FragmentAuthenticationBinding

/**
 * A fragment that asks the users to sign in or register
 * and redirects them back once signed in.
 */
class AuthenticationFragment : Fragment() {
    
    private lateinit var binding: FragmentAuthenticationBinding
    
    private val firebaseLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            // User successfully signed in
            val displayName = FirebaseAuth.getInstance().currentUser?.displayName
            Log.i(TAG, "Successfully signed in user ${displayName}!")
        } else {
            // Sign in failed. If `response` is null, the user canceled the sign-in
            // flow using the back button. Otherwise log the response's error code.
            val errorCode = IdpResponse.fromResultIntent(activityResult.data)?.error?.errorCode
            Log.i(TAG, "Sign in unsuccessful with error code $errorCode")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: a bonus is to customize the sign in flow to look nice using:
        // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogin.setOnClickListener {
            // Launch the FirebaseUI login screen
            launcherFirebaseUILogin()
        }
        setupBackButtonAction()
    }
    
    override fun onResume() {
        super.onResume()
        navigateBackIfAuthenticated()
    }
    
    private fun launcherFirebaseUILogin() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(loginProviders)
            .build()
        firebaseLoginLauncher.launch(signInIntent)
    }
    
    private fun setupBackButtonAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.i(TAG, "Back button pressed")
            AlertDialog.Builder(requireContext())
                .setMessage("Login is required to use this app.")
                .setPositiveButton("Ok") { _, _ ->
                    launcherFirebaseUILogin()
                }
                .setNegativeButton("Quit") { _, _ ->
                    // Close the app.
                    requireActivity().finishAndRemoveTask()
                }
                .show()
        }
    }
    
    private fun navigateBackIfAuthenticated() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            Log.i(TAG, "Navigating back")
            // Go back to the previous fragment
            findNavController().popBackStack()
        }
    }
    
    private companion object {
        const val TAG = "AuthenticationFragment"
    
        // Give users the option to sign in or register with their email or Google account.
        val loginProviders = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
    }
}
