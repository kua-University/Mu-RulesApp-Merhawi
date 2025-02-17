package com.example.mu_rulebook

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChapterContentActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationViewSetting: NavigationView
    private lateinit var navigationViewMenu: NavigationView
    private lateinit var chapterId: String
    private lateinit var sections: MutableList<Pair<String, String>> // Store section titles and content
    private lateinit var currentSectionContent: TextView // To display the selected section's content
    private lateinit var chapterTitleView: TextView // To display the chapter title
    private lateinit var sectionSelectorView: TextView // To display the selected section title
    private lateinit var btnFavourite: ImageButton // To add to favourites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chapter_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the selected chapter ID from the intent
        chapterId = intent.getStringExtra("chapterId") ?: "chapter1"

        // Initialize UI elements
        currentSectionContent = findViewById(R.id.tv_section_content) // TextView to display section content
        chapterTitleView = findViewById(R.id.textView6) // TextView to display chapter title
        sectionSelectorView = findViewById(R.id.tv_section_selector) // TextView to display selected section title
        btnFavourite = findViewById(R.id.btn_favourite)
        FirebaseApp.initializeApp(this)
        // Initialize Firebase Database
        val database = FirebaseDatabase.getInstance().getReference("chapters/$chapterId")

        // Fetch chapter title and sections
        fetchChapterTitleAndSections(database)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationViewSetting = findViewById(R.id.nav_view_setting)
        navigationViewMenu = findViewById(R.id.nav_view_menu)

        // Handle settings button click
        val settingsButton: ImageButton = findViewById(R.id.setting_btn)
        settingsButton.setOnClickListener {
            drawerLayout.openDrawer(navigationViewSetting)
        }

        val menuButton: ImageButton = findViewById(R.id.menu_btn)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(navigationViewMenu)
        }

        // Handle section selector click
        sectionSelectorView.setOnClickListener {
            showSectionSelectorDialog() // Show dialog with section names
        }
        btnFavourite.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is signed in, add to favourites
                val database = FirebaseDatabase.getInstance().getReference("favourites/${user.uid}")
                database.child(chapterId).setValue(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to add to Favourites", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // User is not signed in, redirect to SignInActivity
                Toast.makeText(this, "Please sign in to add favourites", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignIn::class.java))
            }
        }

        // Handle navigation item clicks (Settings Drawer)
        navigationViewSetting.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_about -> {
                    // Navigate to About activity
                    val intent = Intent(this, AboutActivity::class.java)
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
            drawerLayout.closeDrawer(navigationViewSetting)
            true
        }

        // Handle navigation item clicks (Menu Drawer)
        navigationViewMenu.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_chapter_one -> {
                    // Navigate to Chapter 1
                    navigateToChapter("chapter1")
                }
                R.id.nav_chapter_two -> {
                    // Navigate to Chapter 2
                    navigateToChapter("chapter2")
                }
                R.id.nav_chapter_three -> {
                    // Navigate to Chapter 3
                    navigateToChapter("chapter3")
                } R.id.nav_chapter_four -> {
                // Navigate to Chapter 4
                navigateToChapter("chapter4")
            }
            }
            drawerLayout.closeDrawer(navigationViewMenu)
            true
        }



        // Back button to return to Categories activity
        val backButton: ImageButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            finish() // Close the current activity and return to Categories
        }

        // Home button to return to MainActivity
        val homeButton: ImageButton = findViewById(R.id.btn_home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clear the back stack
            startActivity(intent)
        }
    }

    private fun fetchChapterTitleAndSections(database: DatabaseReference) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Fetch and display the chapter title
                    val chapterTitle = snapshot.child("title").getValue(String::class.java)
                    chapterTitleView.text = chapterTitle

                    // Fetch and store sections
                    sections = mutableListOf()
                    val sectionsSnapshot = snapshot.child("sections")
                    for (sectionSnapshot in sectionsSnapshot.children) {
                        val sectionTitle = sectionSnapshot.child("title").getValue(String::class.java)
                        val sectionContent = sectionSnapshot.child("content").getValue(String::class.java)
                        if (sectionTitle != null && sectionContent != null) {
                            sections.add(Pair(sectionTitle, sectionContent))
                        }
                    }

                    // Display the first section by default
                    if (sections.isNotEmpty()) {
                        displaySectionContent(sections[0].second)
                        sectionSelectorView.text = sections[0].first // Set the first section as active
                    }
                } else {
                    Toast.makeText(this@ChapterContentActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChapterContentActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSectionSelectorDialog() {
        if (sections.isEmpty()) {
            Toast.makeText(this, "No sections available", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a list of section titles for the dialog
        val sectionTitles = sections.map { it.first }.toTypedArray()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a Section")
        builder.setItems(sectionTitles) { _, which ->
            // Display the selected section's content
            displaySectionContent(sections[which].second)
            // Update the section selector text
            sectionSelectorView.text = sections[which].first
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun displaySectionContent(content: String) {
        currentSectionContent.text = content
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
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun navigateToChapter(chapterId: String) {
        val intent = Intent(this, ChapterContentActivity::class.java)
        intent.putExtra("chapterId", chapterId) // Pass the chapter ID to the next activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}