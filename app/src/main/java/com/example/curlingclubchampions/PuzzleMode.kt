package com.example.curlingclubchampions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PuzzleMode: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_mode)

        // Temp solve button for demo
        val solveButton = findViewById<Button>(R.id.temp_solve_button)
        solveButton.setOnClickListener {
            val intent = Intent(this@PuzzleMode, PuzzleComplete::class.java)
            finish()
            startActivity(intent)
        }
    }
}