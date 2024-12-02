package com.example.curlingclubchampions.Rock

import android.content.ClipDescription
import android.content.Context
import com.google.gson.Gson

// Handles all information and objects relevant to a given puzzle level
class RockReader {

    // Rock object constructor
    // color = 0 -> Red
    // color = 1 -> Yellow
    data class Rock(
        var colour: Colour,
        var x: Double,
        var y: Double
    )

    // Win Area object constructor, has different variables in json depending on whether its a rectangle or circles
    // types: rectangle, circle
    // rectangle:
    //// Variables: left, top = x, y of top left corner
    //// right, bottom = x, y of bottom right corner
    // circle:
    //// x, y (center), radius
    data class WinArea (
        var type: String,
        var left: Double?,
        var top: Double?,
        var right: Double?,
        var bottom: Double?,
        var x: Double?,
        var y: Double?,
        var radius: Double?
    )

    // Level descriptions for level description, solution description
    data class InfoDesc (
        var description: String
    )
    data class SolutionDesc (
        var description: String
    )

    enum class Colour {
        RED, YELLOW
    }

    // Wrapper class to take array from rocks object in json file
    data class RockListWrapper(val rocks: MutableList<Rock>)

    // Wrapper class to take array of rock sequence in json file for multi rock solutions
    data class RockSequenceListWrapper(val rockSequence: List<Rock>)

    // Wrapper class to take win area from json file
    data class WinAreaWrapper(val winArea: List<WinArea>)

    // Wrapper class to take win area from json file
    data class InfoDescWrapper(val infoDescription: InfoDesc)

    // Wrapper class to take win area from json file
    data class SolutionDescWrapper(val solutionDescription: SolutionDesc)

    // Read contents of json file into a string
    fun readJSON(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    // Parse lines of json string to create a list of rocks
    fun parseJSONToList(jsonString: String): MutableList<Rock> {
        val gson = Gson()
        val rockListWrapper = gson.fromJson(jsonString, RockListWrapper::class.java)
        return rockListWrapper.rocks
    }

    // Parse lines of json string to create a list of rock sequence for multi rock solution
    fun parseJSONToSequenceList(jsonString: String): List<Rock> {
        val gson = Gson()
        val rockSequenceListWrapper = gson.fromJson(jsonString, RockSequenceListWrapper::class.java)
        return rockSequenceListWrapper.rockSequence
    }

    // Parse lines of json string to create win area object
    fun parseJSONToWinArea(jsonString: String): List<WinArea> {
        val gson = Gson()
        val winAreaWrapper = gson.fromJson(jsonString, WinAreaWrapper::class.java)
        return winAreaWrapper.winArea
    }

    // Parse lines of json string to create win area object
    fun parseJSONToInfoDesc(jsonString: String): InfoDesc {
        val gson = Gson()
        val infoDescWrapper = gson.fromJson(jsonString, InfoDescWrapper::class.java)
        return infoDescWrapper.infoDescription
    }

    // Parse lines of json string to create win area object
    fun parseJSONToSolutionDesc(jsonString: String): SolutionDesc {
        val gson = Gson()
        val solutionDescWrapper = gson.fromJson(jsonString, SolutionDescWrapper::class.java)
        return solutionDescWrapper.solutionDescription
    }

}