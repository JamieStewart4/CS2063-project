package com.example.curlingclubchampions.Rock

import android.content.Context
import com.google.gson.Gson


class RockReader {

    // Rock object constructor
    // color = 0 -> Red
    // color = 1 -> Yellow
    data class Rock(
        var colour: Colour,
        var x: Double,
        var y: Double
    )

    enum class Colour {
        RED, YELLOW
    }

    // Wrapper class to take array from rocks object in json file
    data class RockListWrapper(val rocks: List<Rock>)

    // Read contents of json file into a string
    fun readJSON(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    // Parse lines of json string to create a list of rocks
    fun parseJSONToList(jsonString: String): List<Rock> {
        val gson = Gson()
        val rockListWrapper = gson.fromJson(jsonString, RockListWrapper::class.java)
        return rockListWrapper.rocks
    }

}