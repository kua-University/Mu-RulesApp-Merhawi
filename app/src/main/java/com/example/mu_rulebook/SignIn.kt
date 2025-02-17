package com.example.mu_rulebook

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.mu_rulebook.databinding.ActivitySignInBinding // Import the generated binding class

class SignIn : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    // Declare the binding variable

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize View Binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exitButton: Button = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            showExitConfirmationDialogue()

        }
        // Handle Sign In button click (Email/Password)
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInputs(email, password)) {
                signInWithEmail(email, password)
            }
        }

        val btn_sign_up: Button = findViewById(R.id.btn_sign_up)
        // Handle Sign Up button click (Email/Password)
        btn_sign_up.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        val skipBtn: TextView = findViewById(R.id.skip_btn)
        skipBtn.setOnClickListener {
            val intent = Intent(this, Categories::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear the back stack
            startActivity(intent)
        }

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "Password reset email sent to $email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this, "Failed to send reset email: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }




        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Use the string resource
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        // Check if user is already signed in
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Set up Google Sign-In button click listener
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Signed in as ${auth.currentUser?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        return true
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this, "Sign in failed: Please enter Valid Email and Password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
                    Toast.makeText(
                        this, "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this, "Sign up failed: Please enter Valid Email and Password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun showExitConfirmationDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do You Really Want To Exit?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}