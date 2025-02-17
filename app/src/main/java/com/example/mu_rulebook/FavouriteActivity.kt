package com.example.mu_rulebook

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favourite)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("favourites")
Log.d("FavouriteActivity", "Database reference: $database")
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rv_favourites)
        recyclerView.layoutManager = LinearLayoutManager(this)
        favouriteAdapter = FavouriteAdapter(mutableListOf())
        recyclerView.adapter = favouriteAdapter

        // Load favourite chapters for the current user
        loadFavourites()
    }

    private fun loadFavourites() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favourites = mutableListOf<String>()
                    for (favouriteSnapshot in snapshot.children) {
                        val chapterId = favouriteSnapshot.key
                        if (chapterId != null) {
                            favourites.add(chapterId)
                            Log.d("FavouriteActivity", "Loaded chapter ID: $chapterId")
                        }
                    }
                    favouriteAdapter.updateFavourites(favourites)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("FavouriteActivity", "Error loading favourites: ${error.message}")
                    // Handle error
                }
            })
        }
    }
}