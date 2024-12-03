package com.example.curlingclubchampions

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.curlingclubchampions.Rock.RockReader
import com.example.curlingclubchampions.Rock.WinCircle
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

val Context.displayWidth: Int
    get() = resources.displayMetrics.widthPixels
val Context.displayHeight: Int
    get() = resources.displayMetrics.heightPixels

class PuzzleMode: AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetector

    private var puzzleId = -1
    // List of rocks drawn on screen
    private var rockList = mutableListOf<RockReader.Rock>()
    // List of win areas for this puzzle
    private lateinit var winAreaList: List<RockReader.WinArea>
    // Current active win area
    private lateinit var winArea: RockReader.WinArea
    private lateinit var winAreaRect: Rect
    private lateinit var winAreaCirc: WinCircle
    // Sequence of additional rocks added to screen for multi rock solutions
    private lateinit var rockSequence: List<RockReader.Rock>
    // Descriptions for level information + solution
    private lateinit var infoDesc: RockReader.InfoDesc
    private lateinit var solutionDesc: RockReader.SolutionDesc

    // Current active moving rock
    private lateinit var moveRock: RockReader.Rock
    private lateinit var moveRockView: ImageView

    private var redRockDrawable: Drawable? = null
    private var yellowRockDrawable: Drawable? = null

    private var rockHeight: Int = 0
    private var rockWidth: Int = 0

    private var currentRockSequence = 0

    private lateinit var checkmark: ImageView
    private lateinit var exclamationMark: ImageView
    private lateinit var redX: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_mode)
        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)

        window.statusBarColor = ContextCompat.getColor(this, R.color.game_background)

        // Back button functionality
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, PuzzleSelect::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // Loading puzzles
        puzzleId = intent.getIntExtra("PUZZLE_ID", -1)
        if (puzzleId == -1) {
            Log.e("PuzzleMode", "Invalid puzzle ID received!")
            finish()
            return
        }

        Log.i("PuzzleMode", "Loading puzzle ID: $puzzleId")

        // Loads a puzzle level based off of the puzzle ID passed through intent
        val jsonResourceId = when (puzzleId) {
            1 -> R.raw.level_1
            2 -> R.raw.level_2
            3 -> R.raw.level_3
            4 -> R.raw.level_4
            5 -> R.raw.level_5
            6 -> R.raw.level_6
            7 -> R.raw.level_7
            8 -> R.raw.level_8
            9 -> R.raw.level_9
            10 -> R.raw.level_10
            11 -> R.raw.level_11
            12 -> R.raw.level_12
            13 -> R.raw.level_13
            14 -> R.raw.level_14
            //15 -> R.raw.level_15
            //16 -> R.raw.level_16
            //17 -> R.raw.level_17
            //18 -> R.raw.level_18

            else -> {
                Log.e("PuzzleMode", "No JSON file mapped for puzzle ID: $puzzleId")
                Toast.makeText(this, "This puzzle is not created yet.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }

        // Solve button
        val solveButton = findViewById<Button>(R.id.temp_solve_button)
        solveButton.setOnClickListener {
            checkSolution()
        }

        // Get list of rocks and win area from json string for this level
        val rockReader = RockReader()
        val jsonString = rockReader.readJSON(this, jsonResourceId)
        rockList = rockReader.parseJSONToList(jsonString)
        winAreaList = rockReader.parseJSONToWinArea(jsonString)
        // temporary: win area is just index 0 until multi rock logic is done
        winArea = winAreaList[currentRockSequence]

        // Get sequence of rocks to be added for multi rock solutions
        rockSequence = rockReader.parseJSONToSequenceList(jsonString)

        // Get info and solution descriptions for this level
        infoDesc = rockReader.parseJSONToInfoDesc(jsonString)
        solutionDesc = rockReader.parseJSONToSolutionDesc(jsonString)

        redRockDrawable = getDrawable(R.drawable.red_rock)
        yellowRockDrawable = getDrawable(R.drawable.yellow_rock)


        // Add rocks to screen
        for (rock in rockList) {
            val newRock = createImageViewFromRock(rock)
            layout.addView(newRock)
        }

        // Create moveable rock
        moveRock = RockReader.Rock(RockReader.Colour.YELLOW, 475.0, 1900.0)
        // Create image view for moveable rock
        moveRockView = createImageViewFromRock(moveRock)
        layout.addView(moveRockView)

        // Set win area
        createWinArea(winArea)


        // Dimensions for drawable for moving rocks
        moveRockView.post {
            rockHeight = moveRockView.measuredHeight
            rockWidth = moveRockView.measuredWidth
        }

        Log.i("PuzzleMode", "height = $rockHeight , width = $rockWidth")

        gestureDetector = GestureDetector(this, MyGestureListener())

        val infoButton: Button = findViewById(R.id.info_button)

        val builder = AlertDialog.Builder(this)
        val dialogTitleTextView = TextView(this).apply {
            text = "Level $puzzleId"
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 10)
        }
        builder.setCustomTitle(dialogTitleTextView)
        builder.setMessage(infoDesc.description)  // Assuming infoDesc is of type InfoDesc
        builder.setPositiveButton("Start") { dialog, _ -> dialog.dismiss() }
        builder.create().show()

        infoButton.setOnClickListener {
            val newTitleTextView = TextView(this).apply {
                text = "Level $puzzleId"
                textSize = 24f
                gravity = Gravity.CENTER
                setPadding(0, 20, 0, 10)
            }
            builder.setCustomTitle(newTitleTextView)
            builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }

        checkmark = findViewById(R.id.checkmark)
        exclamationMark = findViewById(R.id.exclamation_mark)
        redX = findViewById(R.id.red_x)

    }

    private fun createImageViewFromRock(rock: RockReader.Rock): ImageView {
        val newRock = ImageView(this)
        newRock.x = rock.x.toFloat()
        newRock.y = rock.y.toFloat()
        if (rock.colour == RockReader.Colour.RED) {
            newRock.setImageDrawable(redRockDrawable)
        } else if (rock.colour == RockReader.Colour.YELLOW) {
            newRock.setImageDrawable(yellowRockDrawable)
        }
        return newRock
    }

    private fun createWinArea(winAreaObj: RockReader.WinArea) {
        if (winAreaObj.type == "rectangle") {
            winAreaRect = Rect(winAreaObj.left!!.toInt(), winAreaObj.top!!.toInt(),
                winAreaObj.right!!.toInt(), winAreaObj.bottom!!.toInt()
            )
        } else if (winAreaObj.type == "circle") {
            winAreaCirc = WinCircle(winAreaObj.radius!!, winAreaObj.x!!, winAreaObj.y!!)
        } else {
            Log.e("PuzzleMode", "Invalid win area type: ${winAreaObj.type}")
        }
    }

    // Returns boolean result for whether a rock will intersect another rock
    fun rockHitTest(x: Double, y: Double): Boolean {
        // Check if rock hits any rock in list
        for (rock in rockList) {
            val rockX = rock.x
            val rockY = rock.y

            val rockCircRadius = rockWidth / 2 // padding for rock in image

            val dx = x - rockX
            val dy = y - rockY

            val distance = sqrt(dx.pow(2) + dy.pow(2))
            Log.i("RockHitTest", "distance = $distance, rockCircRadius = $rockCircRadius")
            // Collision if distance between centers is less than sum of radius's - margin of rock image
            if (distance < rockCircRadius * 2 - 20) return true
        }

        // No rock hit
        return false
    }

    fun rockInBoundsTest(x: Double, y: Double): Boolean {
        val location = IntArray(2)
        val houseView = findViewById<ImageView>(R.id.imageView)
        houseView.getLocationOnScreen(location)

        val houseX = location[0]
        val houseY = location[1]
        val houseWidth = houseView.width
        val houseHeight = houseView.height

        // Left and right bounds check
        if (x < houseX || x > houseWidth - rockWidth) {
            Log.i("PuzzleMode", "rockInBoundsTest failed x bounds check: x = $x, y = $y")
            return false
        }
        // Up and down bounds check
        if (y < houseY || y > houseHeight - rockHeight) {
            Log.i("PuzzleMode", "rockInBoundsTest failed y bounds check: x = $x, y = $y")
            return false
        }
        return true
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Send drag events to gesture detector
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }

        checkmark.visibility = ImageView.INVISIBLE
        exclamationMark.visibility = ImageView.INVISIBLE
        redX.visibility = ImageView.INVISIBLE


        // Handle tap to move functionality
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Tap to move functionality
            Log.i("PuzzleMode", "single tap")

            val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)
            // Measured size = 1080, 2126
            Log.i("PuzzleMode", "width: ${layout.measuredWidth}, height: ${layout.measuredHeight}")
            val location = IntArray(2)
            layout.getLocationOnScreen(location)

            // Offset of view from corner
            val offsetX = location[0]
            val offsetY = location[1]

            // Offset of position centered on finger position
            val newX = event.rawX - offsetX - rockWidth / 2
            val newY = event.rawY - offsetY - rockHeight / 2
            // Only move if not intersecting with rock and in bounds
            if (!rockHitTest(newX.toDouble(), newY.toDouble()) && rockInBoundsTest(newX.toDouble(), newY.toDouble())) {
                moveRockView.x = newX
                moveRockView.y = newY
            }
            Log.i("PuzzleMode", "rockHitTest: ${rockHitTest(newX.toDouble(), newY.toDouble())}")
            Log.i("PuzzleMode", "rockInBoundsTest: ${rockInBoundsTest(newX.toDouble(), newY.toDouble())}")
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
                val dx = moveRockView.x - distanceX
                val dy = moveRockView.y - distanceY
                if (!rockHitTest(dx.toDouble(), dy.toDouble()) && rockInBoundsTest(dx.toDouble(), dy.toDouble())) {
                    moveRockView.x -= distanceX
                    moveRockView.y -= distanceY
                } else {
                    isDragging = false
                }
            }
            return true
        }
    }

    private fun checkSolution() {
        // Check if rock is not inside either win area type, if not then display incorrect message
        if (winArea.type == "rectangle") {
            // Check if rectangle win area does not contain x and y of center of rock
            if (!winAreaRect.contains((moveRockView.x + rockWidth / 2).toInt(),
                    (moveRockView.y + rockWidth / 2).toInt()
                )) {
                showRedX()
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show()
                return
            }
        } else if (winArea.type == "circle") {
            // Use Circle rock function to check if not inside
           if (!winAreaCirc.rockInWinArea(moveRockView, rockWidth, rockHeight)) {
               showRedX()
               Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show()
               return
           }
        } else {
            Log.e("PuzzleMode", "Invalid win area type: ${winArea.type}")
            finish()
        }
        // Else, rock must be in win area
        // check if sequence is complete
        if(currentRockSequence + 1 == winAreaList.size) {
            val intent = Intent(this@PuzzleMode, PuzzleComplete::class.java)
            intent.putExtra("PUZZLE_ID", puzzleId)
            intent.putExtra("SOLUTION", solutionDesc.description) // Get string from description
            finish()
            startActivity(intent)
        }
        // else there must be more rock sequence
        else{
            val placedRock = RockReader.Rock(RockReader.Colour.YELLOW, moveRockView.x.toDouble(), moveRockView.y.toDouble())
            rockList.add(placedRock)
            continueRockSequence()
        }

    }

    private fun showRedX() {
        redX.x = (moveRockView.x + rockWidth / 1.5).toFloat()
        redX.y = moveRockView.y - rockHeight
        redX.visibility = ImageView.VISIBLE
        redX.bringToFront()
    }

    private fun continueRockSequence()
    {
        currentRockSequence++
        winArea = winAreaList[currentRockSequence]

        // Move checkmark to previously placed rock to indicate success
        checkmark.x = moveRockView.x + rockWidth / 2
        checkmark.y = moveRockView.y - rockHeight
        checkmark.visibility = ImageView.VISIBLE
        checkmark.bringToFront()

        val nextRock = rockSequence[currentRockSequence - 1]
        rockList.add(nextRock)

        // Move exclamation mark to newly added rock in sequence to indicate update
        exclamationMark.x = (nextRock.x.toFloat() + rockWidth / 1.5).toFloat()
        exclamationMark.y = (nextRock.y - rockHeight * 1.25).toFloat()
        exclamationMark.visibility = ImageView.VISIBLE
        exclamationMark.bringToFront()

        checkIndicatorCollision(nextRock)

        val newRock = createImageViewFromRock(nextRock)
        val layout = findViewById<RelativeLayout>(R.id.puzzle_relative_layout)
        layout.addView(newRock)

        // Create moveable rock
        moveRock = RockReader.Rock(RockReader.Colour.YELLOW, 475.0, 1900.0)
        // Create image view for moveable rock
        moveRockView = createImageViewFromRock(moveRock)
        layout.addView(moveRockView)

        // Set win area
        createWinArea(winArea)

        Log.i("continueRockSequence", "height = $rockHeight , width = $rockWidth")
    }

    // Reposition exclamation mark indicator if it is too close to checkmark
    private fun checkIndicatorCollision(nextRock: RockReader.Rock) {
        if (abs(checkmark.x - exclamationMark.x) < rockWidth) {
            if (abs(exclamationMark.y - checkmark.y) < rockHeight * 1.25) {
                exclamationMark.x = (nextRock.x + rockWidth).toFloat()
                exclamationMark.y = (nextRock.y).toFloat()
            }
        }
    }
}