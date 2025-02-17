package com.example.mu_rulebook

import com.google.firebase.FirebaseApp
import android.view.animation.AnimationUtils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
// Initialize Firebase
        FirebaseApp.initializeApp(this)
        val logo: ImageView = findViewById(R.id.imageView)
        val welcomeText: TextView = findViewById(R.id.textView)
        val button: Button = findViewById(R.id.get_start_btn)

        // Load animations
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Start animations simultaneously
        logo.startAnimation(bounceAnimation)
        welcomeText.startAnimation(fadeInAnimation)

        // Set button click listener
        button.setOnClickListener {
            val intent = Intent(this, Categories::class.java)
            startActivity(intent)
        }
        val exitButton: Button = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            showExitConfirmationDialogue()

        }
    }
override fun onBackPressed(){
AlertDialog.Builder(this)
    .setTitle("Exit")
    .setMessage("Are You Sure You Want to Exit?")
    .setPositiveButton("Yes"){_,_->
        finishAffinity()
    }
    .setNegativeButton("No",null)
    .show()
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
