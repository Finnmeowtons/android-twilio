package com.marasigan.kenth.block6.p1.mangareader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Toolbar with Back Button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Enables Back Button
        supportActionBar?.title = "Favorites"  // Sets Toolbar Title

        // Handle Back Button Click
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()  // Closes Activity
        }
    }
}
