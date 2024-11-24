package com.example.curlingclubchampions

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.curlingclubchampions.adapters.PuzzlePagerAdapter

class PuzzleSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_select)

        val dotsLayout = findViewById<LinearLayout>(R.id.dots_layout)
        dotsLayout.visibility = View.VISIBLE

        // Set up ViewPager2 for swiping
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val dots = listOf(
            findViewById<View>(R.id.dot_easy),
            findViewById<View>(R.id.dot_medium),
            findViewById<View>(R.id.dot_hard)
        )
        viewPager.adapter = PuzzlePagerAdapter(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position, dots)
            }
        })

        updateDots(0, dots)

        // Back button functionality
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
    private fun updateDots(position: Int, dots: List<View>) {
        for (i in dots.indices) {
            if (i == position) {
                dots[i].setBackgroundResource(R.drawable.dot_active)
            } else {
                dots[i].setBackgroundResource(R.drawable.dot_inactive)
            }
        }
    }
}
