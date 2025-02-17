package com.example.mu_rulebook

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavouriteAdapter(private var favourites: MutableList<String>) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite, parent, false)
        return FavouriteViewHolder(view)
        Log.d("FavouriteAdapter", "Favourites size: ${favourites.size}")
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val chapterId = favourites[position]
        holder.bind(chapterId)
        Log.d("FavouriteAdapter", "Binding chapter ID: $chapterId")
    }

    override fun getItemCount(): Int {
        return favourites.size
        Log.d("FavouriteAdapter", "Favourites size: ${favourites.size}")
    }

    fun updateFavourites(newFavourites: List<String>) {
        favourites.clear()
        favourites.addAll(newFavourites)
        notifyDataSetChanged()
        Log.d("FavouriteAdapter", "Favourites size: ${favourites.size}")
    }

    class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvChapterTitle: TextView = itemView.findViewById(R.id.tv_chapter_title)

        fun bind(chapterId: String) {
            tvChapterTitle.text = chapterId // Replace with actual chapter title if available
            Log.d("FavouriteAdapter", "Binding chapter ID: $chapterId")

        }
    }
}