package com.example.curlingclubchampions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.curlingclubchampions.Rock.RockReader
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter

class PuzzleCreator : AppCompatActivity() {

    private val rockList = mutableListOf<RockReader.Rock>() // List to store created rocks
    private var redRockDrawable: Drawable? = null
    private var yellowRockDrawable: Drawable? = null
    private var nextRockColor = RockReader.Colour.RED // Toggle between RED and YELLOW for each new rock

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_creator)
        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)

        if (layout == null) {
            Log.e("PuzzleCreator", "Layout NULL!")
        } else {
            Log.i("PuzzleCreator", "Layout NOT NULL!!!!")
        }

        // Initialize drawables for rock colors
        redRockDrawable = getDrawable(R.drawable.red_rock)
        yellowRockDrawable = getDrawable(R.drawable.yellow_rock)

        // Save button to write all rock locations to a JSON file
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            saveRocksToJsonInExternalStorage()
            val intent = Intent(this@PuzzleCreator, PuzzleComplete::class.java)
            finish()
            startActivity(intent)
        }

        // Set touch listener for layout to detect taps
        layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                // Create a new Rock object with tapped coordinates
                val newRock = RockReader.Rock(nextRockColor, x.toFloat(), y.toFloat())
                rockList.add(newRock)

                // Toggle the color for the next rock
                nextRockColor = if (nextRockColor == RockReader.Colour.RED) {
                    RockReader.Colour.YELLOW
                } else {
                    RockReader.Colour.RED
                }

                // Add the rock to the screen
                addRockToScreen(newRock, layout)
                true
            } else false
        }
    }

    // Function to display a Rock on the screen
    private fun addRockToScreen(rock: RockReader.Rock, layout: RelativeLayout) {
        val newRockView = ImageView(this)
        newRockView.x = rock.x.toFloat()
        newRockView.y = rock.y.toFloat()

        // Set the appropriate drawable for the rock color
        if (rock.colour == RockReader.Colour.RED) {
            newRockView.setImageDrawable(redRockDrawable)
        } else if (rock.colour == RockReader.Colour.YELLOW) {
            newRockView.setImageDrawable(yellowRockDrawable)
        }

        // Add the ImageView to the layout
        layout.addView(newRockView)
    }

    // Function to save rocks to a JSON file
    private fun saveRocksToJsonInExternalStorage() {
        val gson = Gson()
        val jsonString = gson.toJson(rockList)

        Log.i("Puzzle to json", "attempting save to external storage")

        try {
            // Get the app-specific external files directory
            val levelsDir = File(getExternalFilesDir(null), "levels")
            if (!levelsDir.exists()) levelsDir.mkdirs()

            // Create the JSON file
            val fileName = "level_${System.currentTimeMillis()}.json"
            val file = File(levelsDir, fileName)

            FileWriter(file).use { writer ->
                writer.write(jsonString)
                Log.i("PuzzleCreator", "Rocks saved to external storage at ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("PuzzleCreator", "Error saving rocks: ${e.message}")
        }
    }

}
