package com.example.foodfactory.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodfactory.R
import com.example.foodfactory.databinding.FragmentPhoneAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class FragmentPhoneAuth : Fragment() {

    private var signInClient: GoogleSignInClient? = null
    private var gso: GoogleSignInOptions? = null
    val RC_SIGN_IN: Int = 1
    lateinit var binding: FragmentPhoneAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        binding = FragmentPhoneAuthBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("959759804888-r335pdnuqs6lhr2bige9tkaqqkk0vh1e.apps.googleusercontent.com") // not an error
            .requestProfile()
            .build()
        signInClient = GoogleSignIn.getClient(requireActivity(), gso!!)
        binding.signInButton.setOnClickListener { authenticate() }
    }

    private fun authenticate() {
        val signInIntent = signInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Snackbar.make(binding.root, e.message!!, Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(
                requireActivity()
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    binding?.let {
                        Snackbar.make(it.root, "Authentication Success.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    updateUI(user)
                } else {
                    binding?.let {
                        Snackbar.make(it.root, "Authentication Failed.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun moveToMain() {
        findNavController().navigate(R.id.action_nav_phone_to_scanner)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            moveToMain()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

