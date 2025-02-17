package com.example.mu_rulebook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Categories : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Handle settings button click
        val settingsButton: ImageButton = findViewById(R.id.setting_btn)
        settingsButton.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {


                R.id.nav_about -> {
                    // Navigate to About activity
                    val intent = Intent(this, AboutActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                R.id.nav_favourite -> {
                    // Navigate to Feedback activity
                    val intent = Intent(this, FavouriteActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_exit -> {
                    // Exit the app
                    showExitConfirmationDialogue()
                }
                R.id.nav_sign_out -> {
                    // Navigate to MainActivity
                    showSignOutConfirmationDialogue()

                }
            }
            drawerLayout.closeDrawer(navigationView)
            true
        }

        // Back button to return to MainActivity
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {

            finish() // Close the current activity and return to MainActivity
        }

        // Fetch chapter titles from Firebase
        fetchChapterTitles()
    }

    private fun fetchChapterTitles() {
        val database = FirebaseDatabase.getInstance().getReference("chapters")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chapterSnapshot in snapshot.children) {
                    val chapterId = chapterSnapshot.key // e.g., "chapter1"
                    val chapterTitle = chapterSnapshot.child("title").getValue(String::class.java)

                    // Update the UI with chapter titles
                    when (chapterId) {
                        "chapter1" -> setupChapterButton(R.id.chapter_one_btn, chapterTitle, chapterId)
                        "chapter2" -> setupChapterButton(R.id.chapter_two_btn, chapterTitle, chapterId)
                        "chapter3" -> setupChapterButton(R.id.chapter_three_btn, chapterTitle, chapterId)
                        "chapter4" -> setupChapterButton(R.id.chapter_four_btn, chapterTitle, chapterId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Categories, "Failed to load chapters", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupChapterButton(buttonId: Int, chapterTitle: String?, chapterId: String?) {
        val chapterButton: ImageButton = findViewById(buttonId)
        val chapterTitleView: TextView = findViewById(
            when (buttonId) {
                R.id.chapter_one_btn -> R.id.chapter_one
                R.id.chapter_two_btn -> R.id.chapter_two
                R.id.chapter_three_btn -> R.id.chapter_three
                R.id.chapter_four_btn -> R.id.chapter_four
                else -> throw IllegalArgumentException("Invalid button ID")
            }
        )

        // Set the chapter title
        chapterTitleView.text = chapterTitle

        // Set the click listener for the chapter button
        chapterButton.setOnClickListener {
            val intent = Intent(this, ChapterContentActivity::class.java)
            intent.putExtra("chapterId", chapterId) // Pass the chapter ID to the next activity
            startActivity(intent)
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
    private fun showSignOutConfirmationDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do You Really Want To Sign Out?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)

            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}