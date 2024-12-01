package com.example.curlingclubchampions

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PuzzleComplete: AppCompatActivity() {

    private var puzzleID = 0
    private var numLevels = 18

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_complete_screen)

        puzzleID = intent.getIntExtra("PUZZLE_ID", -1)

        // Update completion status for this level
        val preferences = getSharedPreferences("level_status", MODE_PRIVATE)
        with (preferences.edit()) {
            putBoolean(puzzleID.toString(), true)
            apply()
        }

        // Next Level button destroys current activity and goes back into puzzle for sake of demo
        val nextLevelButton = findViewById<Button>(R.id.nextLevelButton)
        nextLevelButton.setOnClickListener {
            val intent = Intent(this@PuzzleComplete, PuzzleMode::class.java)
            finish()
            intent.putExtra("PUZZLE_ID", puzzleID + 1)
            // Only start activity if in bounds
            if (puzzleID + 1 <= numLevels) {
                startActivity(intent)
            }
        }

        val levelCreatorButton = findViewById<Button>(R.id.puzzleCreatorButton)
        levelCreatorButton.setOnClickListener {
            val intent = Intent(this@PuzzleComplete, PuzzleCreator::class.java)
            finish()
            startActivity(intent)
        }

        // Back button (goes back to select mode screen for demo)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        var difficulty = "none"
        if (puzzleID in 1..6) {
            difficulty = "Easy"
        } else if (puzzleID in 7..12) {
            difficulty = "Medium"
        } else if (puzzleID in 13..18) {
            difficulty = "Hard"
        }

        val congratsLevelTextView = findViewById<TextView>(R.id.congratulations_info)
        congratsLevelTextView.text = "Level $puzzleID ($difficulty) complete."

        val starInfoTextView = findViewById<TextView>(R.id.starProgress)
        val totalStars = updateTotalStars(preferences)
        starInfoTextView.text = "$totalStars/18 total stars achieved."
    }

    private fun updateTotalStars(preferences: SharedPreferences): Int {
        var totalStars = 0
        for (i in 1..18) {
            val status = preferences.getBoolean(i.toString(), false)
            if (status) {
                totalStars++
            }
        }
        return totalStars
    }
}