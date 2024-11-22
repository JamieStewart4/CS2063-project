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
    private var isRemoveMode = false // Flag to toggle between placing and removing rocks

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_creator)

        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)

        // Initialize drawables for rock colors
        redRockDrawable = getDrawable(R.drawable.red_rock)
        yellowRockDrawable = getDrawable(R.drawable.yellow_rock)

        // Save button functionality
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            saveRocksToJsonInExternalStorage()
            val intent = Intent(this@PuzzleCreator, PuzzleComplete::class.java)
            finish()
            startActivity(intent)
        }

        // Switch Color button functionality
        val switchColorButton = findViewById<Button>(R.id.switch_color_button)
        switchColorButton.setOnClickListener {
            // Toggle the color
            nextRockColor = if (nextRockColor == RockReader.Colour.RED) {
                RockReader.Colour.YELLOW
            } else {
                RockReader.Colour.RED
            }

            // Log the current color for debugging
            Log.i("PuzzleCreator", "Switched rock color to $nextRockColor")
        }

        // Remove Rocks button functionality
        val removeRockButton = findViewById<Button>(R.id.remove_rock_button)
        removeRockButton.setOnClickListener {
            // Toggle remove mode
            isRemoveMode = !isRemoveMode
            removeRockButton.text = if (isRemoveMode) "Place Rocks" else "Remove Rocks"
            Log.i("PuzzleCreator", "Mode switched to ${if (isRemoveMode) "Remove" else "Place"}")
        }

        // Set touch listener for layout to detect taps
        layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                if (isRemoveMode) {
                    // Handle rock removal
                    removeRockAt(x, y, layout)
                } else {
                    // Create a new Rock object with tapped coordinates
                    val newRock = RockReader.Rock(nextRockColor, x.toDouble(), y.toDouble())
                    rockList.add(newRock)

                    // Add the rock to the screen
                    addRockToScreen(newRock, layout)
                }
                true
            } else false
        }
    }

    // Function to display a Rock on the screen
    private fun addRockToScreen(rock: RockReader.Rock, layout: RelativeLayout) {
        val newRockView = ImageView(this)
        newRockView.x = rock.x.toFloat() - (129 / 2) // 129 is the hard coded value for the rockViews height and width
        newRockView.y = rock.y.toFloat() - (129 / 2) // Changing the h/w will break this solution. but im to lazy to call the values themselves

        // Set the drawable for the rock color
        if (rock.colour == RockReader.Colour.RED) {
            newRockView.setImageDrawable(redRockDrawable)
        } else if (rock.colour == RockReader.Colour.YELLOW) {
            newRockView.setImageDrawable(yellowRockDrawable)
        }

        // Set a tag to identify the view and associate it with the rock object. (used in remove rock)
        newRockView.tag = rock

        newRockView.setOnClickListener {
            if (isRemoveMode) {
                layout.removeView(newRockView)
                rockList.remove(rock)
                Log.i("PuzzleCreator", "Rock removed: $rock")
            }
        }

        layout.addView(newRockView)
    }

    // Function to remove a rock at a specific location
    private fun removeRockAt(x: Float, y: Float, layout: RelativeLayout) {
        // Find the rock closest to the tapped location
        val iterator = rockList.iterator()
        while (iterator.hasNext()) {
            val rock = iterator.next()
            val dx = x - rock.x.toFloat()
            val dy = y - rock.y.toFloat()
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()) // this is the vector length of the tapped location to the current rock we are iterating over

            if (distance < 50) {
                // Remove the rock from the layout and the list
                val rockView = layout.findViewWithTag<View>(rock) //remove this bich from the layout (Displayed rock)
                layout.removeView(rockView)
                iterator.remove()
                Log.i("PuzzleCreator", "Rock removed at position: ($x, $y)")
                break
            }
        }
    }

    // Function to save rocks to a JSON file
    private fun saveRocksToJsonInExternalStorage() {
        val gson = Gson()
        val jsonString = gson.toJson(rockList)

        try {
            val levelsDir = File(getExternalFilesDir(null), "levels")
            if (!levelsDir.exists()) levelsDir.mkdirs()

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

