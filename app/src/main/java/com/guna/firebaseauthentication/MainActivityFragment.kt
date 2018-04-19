package com.guna.firebaseauthentication

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private val RC_SIGN_IN = 123

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        refreshUI(user)

        btnSignIn.setOnClickListener({
            signIn()
        })

        btnSignOut.setOnClickListener({
            signOut()
        })

        btnDelete.setOnClickListener({
            delete();
        })
    }

    private fun delete() {
        activity?.let {
            AuthUI.getInstance()
                    .delete(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, getString(R.string.deleted), LENGTH_LONG).show()
                        }
                        val user = FirebaseAuth.getInstance().currentUser
                        refreshUI(user)
                    }
        }
    }

    private fun refreshUI(user: FirebaseUser?) {
        if (user == null) {
            txtName.text = getString(R.string.sign_in_required)
            btnSignIn.visibility = View.VISIBLE
            btnSignOut.visibility = View.GONE
            btnDelete.visibility = View.GONE
        } else {
            val name = user.displayName
            if (name != null && !name.isBlank())
                txtName.text = getString(R.string.sign_in, user.displayName)
            else
                txtName.text = getString(R.string.sign_in, user.phoneNumber)
            btnSignIn.visibility = View.GONE
            btnDelete.visibility = View.VISIBLE
            btnSignOut.visibility = View.VISIBLE
        }
    }

    private fun signOut() {
        activity?.let {
            AuthUI.getInstance()
                    .signOut(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, getString(R.string.signed_out), LENGTH_LONG).show()
                            txtName.text = getString(R.string.signed_out)
                            val user = FirebaseAuth.getInstance().currentUser
                            refreshUI(user)
                        }
                    }
        }
    }

    private fun signIn() {
        // Choose authentication providers
        val providers = Arrays.asList(AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(activity, "Successfully logged in", LENGTH_LONG).show()
                refreshUI(user)
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }
}
