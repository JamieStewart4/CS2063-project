package com.example.curlingclubchampions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.curlingclubchampions.Rock.RockReader
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter

class PuzzleCreator : AppCompatActivity() {

    private val rockList = mutableListOf<RockReader.Rock>() // List to store created rocks
    private var redRockDrawable: Drawable? = null
    private var yellowRockDrawable: Drawable? = null
    private var nextRockColor = RockReader.Colour.RED // Toggle between RED and YELLOW for each new rock
    private val winAreaList = mutableListOf<Map<String, Any>>() // List to store win areas

    private var currentMode: Mode = Mode.PLACE_ROCKS // Default mode
    private enum class Mode {
        PLACE_ROCKS, REMOVE_ROCKS, PLACE_WIN_AREA, REMOVE_WIN_AREA
    }

    private var startX = 0f
    private var startY = 0f

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
            saveDataToJson()
            val intent = Intent(this@PuzzleCreator, PuzzleComplete::class.java)
            finish()
            startActivity(intent)
        }

        // Switch Color button functionality
        val switchColorButton = findViewById<Button>(R.id.switch_color_button)
        switchColorButton.setOnClickListener {
            nextRockColor = if (nextRockColor == RockReader.Colour.RED) {
                RockReader.Colour.YELLOW
            } else {
                RockReader.Colour.RED
            }
            Log.i("PuzzleCreator", "Switched rock color to $nextRockColor")
        }

        // Remove Rocks button functionality
        val rockButton = findViewById<Button>(R.id.rock_button)
        rockButton.setOnClickListener {
            currentMode = when (currentMode) {
                Mode.PLACE_ROCKS -> {
                    rockButton.text = "Remove Rocks"
                    Mode.REMOVE_ROCKS
                }
                Mode.REMOVE_ROCKS -> {
                    rockButton.text = "Place Rocks"
                    Mode.PLACE_ROCKS
                }
                else -> {
                    rockButton.text = "Place Rocks"
                    Mode.PLACE_ROCKS
                }
            }
            Log.i("PuzzleCreator", "Current mode: $currentMode")
        }

        // Place Win Area button functionality
        val winAreaButton = findViewById<Button>(R.id.win_area_button)
        winAreaButton.setOnClickListener {
            currentMode = when (currentMode) {
                Mode.PLACE_WIN_AREA -> {
                    winAreaButton.text = "Remove Win Areas"
                    Mode.REMOVE_WIN_AREA
                }
                Mode.REMOVE_WIN_AREA -> {
                    winAreaButton.text = "Place Win Areas"
                    Mode.PLACE_WIN_AREA
                }
                else -> {
                    winAreaButton.text = "Place Win Areas"
                    Mode.PLACE_WIN_AREA
                }
            }
            Log.i("PuzzleCreator", "Current mode: $currentMode")
        }

        // Set touch listener for layout
        layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Store the starting touch coordinates
                val x = event.x
                val y = event.y

                // Handle different modes
                when (currentMode) {
                    Mode.PLACE_ROCKS -> addRock(x, y, layout)
                    Mode.REMOVE_WIN_AREA -> {
                        removeWinAreaAt(x, y, layout)
                        return@setOnTouchListener true // Stop further propagation if a win area is removed
                    }
                    Mode.REMOVE_ROCKS -> {
                        removeRockAt(x, y, layout)
                        return@setOnTouchListener true // Stop further propagation if a rock is removed
                    }
                    else -> {
                        startX = event.x
                        startY = event.y
                    }
                }
                true

            } else if (event.action == MotionEvent.ACTION_UP) {
                // Calculate the distance from the starting touch position (startX, startY) to the release position (event.x, event.y)
                val radius = calculateDistance(startX, startY, event.x, event.y)

                // If we are in "Place Win Area" mode, add the win area with the dynamic radius
                if (currentMode == Mode.PLACE_WIN_AREA) {
                    addWinArea(startX, startY, radius, layout)
                    startX = 0f
                    startY = 0f
                }

                true
            } else {
                false
            }
        }

    }
    // Function to calculate the distance between two points
    private fun calculateDistance(startX: Float, startY: Float, endX: Float, endY: Float): Float {
        val dx = endX - startX
        val dy = endY - startY
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    // Function to add a rock
    private fun addRock(x: Float, y: Float, layout: RelativeLayout) {
        val newRock = RockReader.Rock(nextRockColor, x.toDouble(), y.toDouble())
        rockList.add(newRock)
        addRockToScreen(newRock, layout)
    }

    private fun removeRockAt(x: Float, y: Float, layout: RelativeLayout) {
        val iterator = rockList.iterator()
        while (iterator.hasNext()) {
            val rock = iterator.next()
            val dx = x - rock.x.toFloat()
            val dy = y - rock.y.toFloat()
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble())

            if (distance <= 50) {
                val childToRemove = layout.children.find { child ->
                    child.tag == rock
                }

                if (childToRemove != null) {
                    layout.removeView(childToRemove)
                    iterator.remove()
                    Log.i("PuzzleCreator", "Rock removed at position: (${rock.x}, ${rock.y})")
                } else {
                    Log.w("PuzzleCreator", "No view found for rock at position: (${rock.x}, ${rock.y})")
                }
                return
            }
        }
        Log.w("PuzzleCreator", "No rock found near: ($x, $y)")
    }

    // Function to display a rock on the screen
    private fun addRockToScreen(rock: RockReader.Rock, layout: RelativeLayout) {
        val newRockView = ImageView(this).apply {
            x = rock.x.toFloat() - (129 / 2)  // Adjust the position based on the rock's size
            y = rock.y.toFloat() - (129 / 2)
            setImageDrawable(if (rock.colour == RockReader.Colour.RED) redRockDrawable else yellowRockDrawable)
            tag = rock

            // Make the view transparent to touch events
            isClickable = false
            isFocusable = false

            // Set the removal behavior when clicked
            setOnClickListener {
                if (currentMode == Mode.REMOVE_ROCKS) {
                    layout.removeView(this)
                    rockList.remove(rock)
                    Log.i("PuzzleCreator", "Rock removed: $rock")
                }
            }
        }

        layout.addView(newRockView)
    }

    // Function to add a win circle to the screen
    private fun addWinArea(x: Float, y: Float, radius: Float, layout: RelativeLayout) {
        val winCircleView = View(this)

        // Set the size of the win circle
        val circleDiameter = (radius * 2).toInt()

        // Create a circular drawable with a semi-transparent green color
        val circleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#8033CC33")) // Semi-transparent green (#80 = 50% opacity)
        }

        winCircleView.background = circleDrawable

        // Set the size and position of the circle
        val layoutParams = RelativeLayout.LayoutParams(circleDiameter, circleDiameter).apply {
            leftMargin = (x - radius).toInt()
            topMargin = (y - radius).toInt()
        }
        winCircleView.layoutParams = layoutParams

        // Make the view transparent to touch events
        winCircleView.isClickable = false
        winCircleView.isFocusable = false

        // Set a tag to identify the win area
        val winAreaDetails = mapOf(
            "type" to "circle",
            "x" to x,
            "y" to y,
            "radius" to radius
        )
        winCircleView.tag = winAreaDetails

        // Add a click listener for removing the win area
        winCircleView.setOnClickListener {
            if (currentMode == Mode.REMOVE_WIN_AREA) {
                layout.removeView(winCircleView)
                winAreaList.remove(winAreaDetails)
                Log.i("PuzzleCreator", "Win area removed: $winAreaDetails")
            }
        }

        layout.addView(winCircleView)
        winAreaList.add(winAreaDetails)
    }

    private fun removeWinAreaAt(x: Float, y: Float, layout: RelativeLayout) {
        val iterator = winAreaList.iterator()
        while (iterator.hasNext()) {
            val winArea = iterator.next()
            val winX = (winArea["x"] as Float)
            val winY = (winArea["y"] as Float)
            val radius = (winArea["radius"] as Float)

            val dx = x - winX
            val dy = y - winY
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble())

            if (distance <= radius) {
                // Remove the view from the layout
                val childToRemove = layout.children.find { child ->
                    child.tag == winArea
                }

                if (childToRemove != null) {
                    layout.removeView(childToRemove)
                    iterator.remove()
                    Log.i("PuzzleCreator", "Win area removed at position: ($winX, $winY)")
                }
                return
            }
        }
        Log.w("PuzzleCreator", "No win area found near: ($x, $y)")
    }

    // Function to save data (rocks and win circles) to JSON
    private fun saveDataToJson() {
        val gson = Gson()
        val jsonData = mapOf(
            "rocks" to rockList,       // Serialize the rock list
            "winArea" to winAreaList // Serialize the win area list
        )
        val jsonString = gson.toJson(jsonData)

        try {
            val levelsDir = File(getExternalFilesDir(null), "levels")
            if (!levelsDir.exists()) levelsDir.mkdirs()

            val fileName = "level_${System.currentTimeMillis()}.json"
            val file = File(levelsDir, fileName)

            FileWriter(file).use { writer ->
                writer.write(jsonString)
                Log.i("PuzzleCreator", "Data saved to external storage at ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("PuzzleCreator", "Error saving data: ${e.message}")
        }
    }
}