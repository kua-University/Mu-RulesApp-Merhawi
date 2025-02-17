package com.example.mu_rulebook

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mu_rulebook.databinding.ActivitySignInBinding
import com.example.mu_rulebook.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        // Initialize View Binding


        val exitButton: Button = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            showExitConfirmationDialogue()

        }


        // Handle Sign Up button click (Email/Password)
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInputs(email, password)) {
                signUpWithEmail(email, password)
            }
        }
        val btn_sign_in : Button = findViewById(R.id.btn_sign_in)
        btn_sign_in.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


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
                        this, "Sign up failed: ${task.exception?.message}",
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