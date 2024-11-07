package com.example.curlingclubchampions

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.curlingclubchampions.Rock.RockReader

class PuzzleMode: AppCompatActivity() {
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
        val rockList = rockReader.parseJSONToList(jsonString)

        val redRockDrawable: Drawable? = getDrawable(R.drawable.red_rock)
        val yellowRockDrawable: Drawable? = getDrawable(R.drawable.yellow_rock)

        for (rock in rockList) {
            val newRock = ImageView(this)
            newRock.x = rock.x
            newRock.y = rock.y
            if (rock.colour == RockReader.Colour.RED) {
                newRock.setImageDrawable(redRockDrawable)
            } else if (rock.colour == RockReader.Colour.YELLOW) {
                newRock.setImageDrawable(yellowRockDrawable)
            }
            layout.addView(newRock)
        }
    }
}