package com.example.curlingclubchampions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectMode: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_mode)

        val puzzleButton = findViewById<Button>(R.id.puzzle_button)
        puzzleButton.setOnClickListener {
            val intent = Intent(this@SelectMode, PuzzleMode::class.java)
            startActivity(intent)
        }

        val puzzleCreatorButton = findViewById<Button>(R.id.free_play_button)
        puzzleCreatorButton.setOnClickListener {
            val intent = Intent(this@SelectMode, PuzzleCreator::class.java)
            startActivity(intent)
        }

        // Back button
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}