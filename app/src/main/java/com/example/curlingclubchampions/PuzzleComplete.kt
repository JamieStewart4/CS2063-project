package com.example.curlingclubchampions

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PuzzleComplete: AppCompatActivity() {

    private var puzzleID = 0
    private var numLevels = 15

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_complete_screen)

        window.statusBarColor = ContextCompat.getColor(this, R.color.button)

        puzzleID = intent.getIntExtra("PUZZLE_ID", -1)
        var solutionDesc = intent.getStringExtra("SOLUTION")

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

        val retryButton = findViewById<Button>(R.id.puzzleCreatorButton)
        retryButton.setOnClickListener {
            val intent = Intent(this@PuzzleComplete, PuzzleMode::class.java)
            finish()
            intent.putExtra("PUZZLE_ID", puzzleID)
            startActivity(intent)
        }

        // Hold retry button to go to level creator
        retryButton.setOnLongClickListener {
            val intent = Intent(this@PuzzleComplete, PuzzleCreator::class.java)
            finish()
            startActivity(intent)
            true
        }

        // Back button (goes back to select mode screen for demo)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, PuzzleSelect::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        var difficulty = "none"
        if (puzzleID in 1..5) {
            difficulty = "Easy"
        } else if (puzzleID in 6..10) {
            difficulty = "Medium"
        } else if (puzzleID in 11..15) {
            difficulty = "Hard"
        }

        val congratsLevelTextView = findViewById<TextView>(R.id.congratulations_info)
        congratsLevelTextView.text = "Level $puzzleID ($difficulty) complete."

        val solutionDescriptionTextView = findViewById<TextView>(R.id.solutionDescriptionText)
        solutionDescriptionTextView.text = "Explanation:\n\n$solutionDesc"

        val starInfoTextView = findViewById<TextView>(R.id.starProgress)
        val totalStars = updateTotalStars(preferences)
        starInfoTextView.text = "$totalStars/15 total stars achieved."
    }

    private fun updateTotalStars(preferences: SharedPreferences): Int {
        var totalStars = 0
        for (i in 1..numLevels) {
            val status = preferences.getBoolean(i.toString(), false)
            if (status) {
                totalStars++
            }
        }
        return totalStars
    }
}