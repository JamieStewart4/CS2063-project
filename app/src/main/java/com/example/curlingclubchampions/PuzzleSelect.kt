package com.example.curlingclubchampions

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.curlingclubchampions.adapters.PuzzlePagerAdapter

class PuzzleSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_select)

        // Set up ViewPager2 for swiping
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = PuzzlePagerAdapter(this)

        // Back button functionality
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}
