package com.example.curlingclubchampions

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.curlingclubchampions.Rock.RockReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.math.sqrt

class PuzzleMode: AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetector

    private lateinit var rockList: List<RockReader.Rock>
    private lateinit var moveRock: RockReader.Rock
    private lateinit var moveRockView: ImageView

    private var redRockDrawable: Drawable? = null
    private var yellowRockDrawable: Drawable? = null

    private var rockHeight: Int = 0
    private var rockWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_mode)
        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)

        if (layout == null) {
            Log.e("PuzzleMode", "Layout NULL!")
        } else {
            Log.i("PuzzleMode", "Layout NOT NULL!!!!")
        }

        // Temp solve button for demo
        val solveButton = findViewById<Button>(R.id.temp_solve_button)
        solveButton.setOnClickListener {
            val intent = Intent(this@PuzzleMode, PuzzleComplete::class.java)
            finish()
            startActivity(intent)
        }

        val rockReader = RockReader()
        val jsonString = rockReader.readJSON(this, R.raw.level_1)
        rockList = rockReader.parseJSONToList(jsonString)

        redRockDrawable = getDrawable(R.drawable.red_rock)
        yellowRockDrawable = getDrawable(R.drawable.yellow_rock)


        // Add rocks to screen
        for (rock in rockList) {
            val newRock = ImageView(this)
            newRock.x = rock.x.toFloat()
            newRock.y = rock.y.toFloat()
            if (rock.colour == RockReader.Colour.RED) {
                newRock.setImageDrawable(redRockDrawable)
            } else if (rock.colour == RockReader.Colour.YELLOW) {
                newRock.setImageDrawable(yellowRockDrawable)
            }
            layout.addView(newRock)
        }

        // Create moveable rock
        moveRock = RockReader.Rock(RockReader.Colour.YELLOW, 475.0, 1900.0)
        // Create image view for moveable rock
        moveRockView = ImageView(this)
        moveRockView.x = moveRock.x.toFloat()
        moveRockView.y = moveRock.y.toFloat()
        moveRockView.setImageDrawable(yellowRockDrawable)
        layout.addView(moveRockView)

        // Dimensions for drawable for moving rocks
        moveRockView.post {
            rockHeight = moveRockView.measuredHeight
            rockWidth = moveRockView.measuredWidth
        }

        Log.i("PuzzleMode", "height = $rockHeight , width = $rockWidth")

        gestureDetector = GestureDetector(this, MyGestureListener())
    }

    // Returns boolean result for whether a rock will intersect another rock
    fun rockHitTest(x: Double, y: Double): Boolean {
        // Get current rock bounding box from coordinates

        val moveRockLeft = x
        val moveRockRight = x + rockWidth
        val moveRockTop = y
        val moveRockBottom = y + rockHeight

        // Check if rock hits any rock in list
        for (rock in rockList) {
            /*val rockX = rock.x
            val rockY = rock.y

            val rockCircRadius = rockWidth / 2 // padding for rock in image

            val dx = x - rockX
            val dy = y - rockY

            val distance = sqrt(dx.pow(2) + dy.pow(2))
            Log.i("RockHitTest", "distance = $distance, rockCircRadius = $rockCircRadius")
            if (distance < rockCircRadius) return true*/

            val rockLeft = rock.x
            val rockTop = rock.y
            val rockRight = rock.x + rockWidth
            val rockBottom = rock.y + rockHeight

            val horizontalOverlap = moveRockRight > rockLeft && moveRockLeft < rockRight
            val verticalOverlap = moveRockBottom > rockTop && moveRockTop < rockBottom

            if (horizontalOverlap && verticalOverlap) return true
        }

        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Send drag events to gesture detector
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }

        // Handle tap to move functionality
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Tap to move functionality
            Log.i("PuzzleMode", "single tap")

            val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)
            val location = IntArray(2)
            layout.getLocationOnScreen(location)

            val offsetX = location[0]
            val offsetY = location[1]

            val newX = event.rawX - offsetX - rockWidth / 2
            val newY = event.rawY - offsetY - rockHeight / 2
            // Only move if not intersecting with rock
            if (!rockHitTest(newX.toDouble(), newY.toDouble())) {
                moveRockView.x = newX
                moveRockView.y = newY
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    internal inner class MyGestureListener: SimpleOnGestureListener() {
        private var isDragging = false

        override fun onDown(e: MotionEvent): Boolean {
            // Get position of rock view on screen
            val location = IntArray(2)
            moveRockView.getLocationOnScreen(location)
            val rockX = location[0]
            val rockY = location[1]

            // Set dragging status to true if rock is currently being pressed
            val rockRect = Rect(
                rockX,
                rockY,
                rockX + rockWidth,
                rockY + rockHeight
            )
            isDragging = rockRect.contains(e.rawX.toInt(), e.rawY.toInt())
            Log.i("MyGestureListener", "isDragging = $isDragging, x = ${e.rawX}, y = ${e.rawY}, rect = $rockRect")
            return isDragging
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (isDragging) {
                Log.i("MyGestureListener", "distanceX = $distanceX, distanceY = $distanceY")
                if (!rockHitTest((moveRockView.x - distanceX).toDouble(), (moveRockView.y - distanceY).toDouble()))
                moveRockView.x -= distanceX
                moveRockView.y -= distanceY
            }
            return true
        }
    }
}