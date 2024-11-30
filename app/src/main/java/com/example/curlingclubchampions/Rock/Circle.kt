package com.example.curlingclubchampions.Rock

import android.util.Log
import android.widget.ImageView
import kotlin.math.pow
import kotlin.math.sqrt

class WinCircle(val radius: Double, val x: Double, val y: Double) {

    fun rockInWinArea(rockView: ImageView, rockWidth: Int, rockHeight: Int): Boolean {
        Log.i("WinCircle", "${this.x} - ${rockView.x} + ${rockWidth / 2}")
        Log.i("WinCircle", "${this.y} - ${rockView.y} + ${rockHeight / 2}")
        val dx = this.x - (rockView.x + rockWidth / 2)
        val dy = this.y - (rockView.y + rockHeight / 2)

        Log.i("WinCircle", "rockInWinArea dx = $dx, dy = $dy")

        val distance = sqrt(dx.pow(2) + dy.pow(2))

        Log.i("WinCircle", "rockInWinArea distance = $distance")

        // Rock is in win area if the distance between win center and rock center plus rock radius is less than win radius
        return distance < this.radius
    }

}

