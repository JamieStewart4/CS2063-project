package com.example.curlingclubchampions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PuzzleComplete: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_complete_screen)

        // Next Level button destroys current activity and goes back into puzzle for sake of demo
        val nextLevelButton = findViewById<Button>(R.id.nextLevelButton)
        nextLevelButton.setOnClickListener {
            val intent = Intent(this@PuzzleComplete, PuzzleMode::class.java)
            finish()
            startActivity(intent)
        }

        // Back button (goes back to select mode screen for demo)
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}