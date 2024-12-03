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
import androidx.core.content.ContextCompat
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
        PLACE_ROCKS, REMOVE_ROCKS, PLACE_CIRCLE_WIN_AREA, REMOVE_CIRCLE_WIN_AREA,  PLACE_RECTANGLE_WIN_AREA, REMOVE_RECTANGLE_WIN_AREA
    }

    private var startX = 0f
    private var startY = 0f

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.puzzle_creator)

        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)

        window.statusBarColor = ContextCompat.getColor(this, R.color.game_background)

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

        val resetButton = findViewById<Button>(R.id.reset_all_progress)
        resetButton.setOnClickListener {
            resetAllProgress()
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
                Mode.PLACE_CIRCLE_WIN_AREA -> {
                    winAreaButton.text = "Remove Win Areas"
                    Mode.REMOVE_CIRCLE_WIN_AREA
                }
                Mode.REMOVE_CIRCLE_WIN_AREA -> {
                    winAreaButton.text = "Place Win Areas"
                    Mode.PLACE_CIRCLE_WIN_AREA
                }
                else -> {
                    winAreaButton.text = "Place Win Areas"
                    Mode.PLACE_CIRCLE_WIN_AREA
                }
            }
            Log.i("PuzzleCreator", "Current mode: $currentMode")
        }

        val rectangleWinAreaButton = findViewById<Button>(R.id.rectangle_win_area_button)
        rectangleWinAreaButton.setOnClickListener {
            currentMode = when (currentMode) {
                Mode.PLACE_RECTANGLE_WIN_AREA -> {
                    rectangleWinAreaButton.text = "Remove Rectangle Win Areas"
                    Mode.REMOVE_RECTANGLE_WIN_AREA
                }
                Mode.REMOVE_RECTANGLE_WIN_AREA -> {
                    rectangleWinAreaButton.text = "Place Rectangle Win Areas"
                    Mode.PLACE_RECTANGLE_WIN_AREA
                }
                else -> {
                    rectangleWinAreaButton.text = "Place Rectangle Win Areas"
                    Mode.PLACE_RECTANGLE_WIN_AREA
                }
            }
            Log.i("PuzzleCreator", "Current mode: $currentMode")
        }

        // Set touch listener for layout
        layout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    when (currentMode) {
                        Mode.PLACE_ROCKS -> addRock(startX, startY, layout)
                        Mode.REMOVE_ROCKS -> removeRockAt(startX, startY, layout)
                        Mode.PLACE_CIRCLE_WIN_AREA -> {
                            val radius = calculateDistance(startX, startY, endX, endY)
                            addCircleWinArea(startX, startY, radius, layout)
                        }
                        Mode.REMOVE_CIRCLE_WIN_AREA -> removeCircleWinAreaAt(startX, startY, layout)
                        Mode.PLACE_RECTANGLE_WIN_AREA -> {
                            addRectangleWinArea(startX, startY, endX, endY, layout)
                        }
                        Mode.REMOVE_RECTANGLE_WIN_AREA -> removeRectangleWinAreaAt(startX, startY, layout)
                    }
                    startX = 0f
                    startY = 0f
                    true
                }
                else -> false
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
    private fun addCircleWinArea(x: Float, y: Float, radius: Float, layout: RelativeLayout) {
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
            if (currentMode == Mode.REMOVE_CIRCLE_WIN_AREA) {
                layout.removeView(winCircleView)
                winAreaList.remove(winAreaDetails)
                Log.i("PuzzleCreator", "Win area removed: $winAreaDetails")
            }
        }

        layout.addView(winCircleView)
        winAreaList.add(winAreaDetails)
    }

    private fun removeCircleWinAreaAt(x: Float, y: Float, layout: RelativeLayout) {
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

    private fun addRectangleWinArea(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        layout: RelativeLayout
    ) {
        val winRectangleView = View(this)

        val left = minOf(x1, x2)
        val top = minOf(y1, y2)
        val right = maxOf(x1, x2)
        val bottom = maxOf(y1, y2)

        val layoutParams = RelativeLayout.LayoutParams(
            (right - left).toInt(),
            (bottom - top).toInt()
        ).apply {
            leftMargin = left.toInt()
            topMargin = top.toInt()
        }

        val rectangleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#8033CC33")) // Semi-transparent green
        }
        winRectangleView.background = rectangleDrawable
        winRectangleView.layoutParams = layoutParams

        val winAreaDetails = mapOf(
            "type" to "rectangle",
            "left" to left,
            "top" to top,
            "right" to right,
            "bottom" to bottom
        )
        winRectangleView.tag = winAreaDetails

        winRectangleView.setOnClickListener {
            if (currentMode == Mode.REMOVE_RECTANGLE_WIN_AREA) {
                layout.removeView(winRectangleView)
                winAreaList.remove(winAreaDetails)
                Log.i("PuzzleCreator", "Rectangle win area removed: $winAreaDetails")
            }
        }

        layout.addView(winRectangleView)
        winAreaList.add(winAreaDetails)
    }

    private fun removeRectangleWinAreaAt(x: Float, y: Float, layout: RelativeLayout) {
        val iterator = winAreaList.iterator()
        while (iterator.hasNext()) {
            val winArea = iterator.next()
            if (winArea["type"] == "rectangle") {
                val x1 = winArea["x1"] as Float
                val y1 = winArea["y1"] as Float
                val x2 = winArea["x2"] as Float
                val y2 = winArea["y2"] as Float

                if (x in x1..x2 && y in y1..y2) {
                    val childToRemove = layout.children.find { child ->
                        child.tag == winArea
                    }
                    if (childToRemove != null) {
                        layout.removeView(childToRemove)
                        iterator.remove()
                        Log.i("PuzzleCreator", "Rectangle win area removed at: ($x1, $y1), ($x2, $y2)")
                    }
                    return
                }
            }
        }
        Log.w("PuzzleCreator", "No rectangle win area found near: ($x, $y)")
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

    private fun resetAllProgress() {
        val preferences = getSharedPreferences("level_status", MODE_PRIVATE)
        with (preferences.edit()) {
            for (i in 1..15) {
                putBoolean(i.toString(), false)
            }
            apply()
        }
    }

}