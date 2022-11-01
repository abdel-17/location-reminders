package com.udacity.project4.authentication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAuthenticationBinding
import kotlin.math.log

// TODO update the documentation to RemindersFragment
/**
 * A fragment that asks the users to sign in / register and redirects
 * signed in users to the RemindersActivity.
 */
class AuthenticationFragment : Fragment() {
    
    private lateinit var binding: FragmentAuthenticationBinding
    
    private val firebaseLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        // Used for logging purposes
        val tag = this.javaClass.simpleName
        if (activityResult.resultCode == Activity.RESULT_OK) {
            // User successfully signed in
            val displayName = FirebaseAuth.getInstance().currentUser?.displayName
            Log.i(tag, "Successfully signed in user ${displayName}!")
        } else {
            // Sign in failed. If `response` is null, the user canceled the sign-in
            // flow using the back button. Otherwise log the response's error code.
            when (val response = IdpResponse.fromResultIntent(activityResult.data)) {
                null -> Log.i(tag, "Sign in cancelled")
                else -> Log.i(tag, "Sign in unsuccessful ${response.error?.errorCode}")
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: If the user was authenticated, send him to RemindersActivity
        
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
    }
    
    private fun launcherFirebaseUILogin() {
        // Give users the option to sign in or register with their email or Google account.
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        firebaseLoginLauncher.launch(signInIntent)
    }
}
