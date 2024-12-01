package com.example.curlingclubchampions.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.curlingclubchampions.PuzzleMode
import com.example.curlingclubchampions.R

class MediumFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_medium, container, false)

        val titleTextView: TextView = view.findViewById(R.id.title_medium)
        titleTextView.text = "Medium"

        val preferences = requireContext().getSharedPreferences("level_status", Context.MODE_PRIVATE)

        val buttonLevel7 = view.findViewById<Button>(R.id.level_7)
        buttonLevel7.setOnClickListener {
            startPuzzleMode(7)
        }
        updateButtonStatus(preferences, buttonLevel7, 7.toString())

        val buttonLevel8 = view.findViewById<Button>(R.id.level_8)
        buttonLevel8.setOnClickListener {
            startPuzzleMode(8)
        }
        updateButtonStatus(preferences, buttonLevel8, 8.toString())

        val buttonLevel9 = view.findViewById<Button>(R.id.level_9)
        buttonLevel9.setOnClickListener {
            startPuzzleMode(9)
        }
        updateButtonStatus(preferences, buttonLevel9, 9.toString())

        val buttonLevel10 = view.findViewById<Button>(R.id.level_10)
        buttonLevel10.setOnClickListener {
            startPuzzleMode(10)
        }
        updateButtonStatus(preferences, buttonLevel10, 10.toString())

        val buttonLevel11 = view.findViewById<Button>(R.id.level_11)
        buttonLevel11.setOnClickListener {
            startPuzzleMode(11)
        }
        updateButtonStatus(preferences, buttonLevel11, 11.toString())

        val buttonLevel12 = view.findViewById<Button>(R.id.level_12)
        buttonLevel12.setOnClickListener {
            startPuzzleMode(12)
        }
        updateButtonStatus(preferences, buttonLevel12, 12.toString())

        return view
    }

    private fun startPuzzleMode(puzzleId: Int) {
        val intent = Intent(requireContext(), PuzzleMode::class.java)
        intent.putExtra("PUZZLE_ID", puzzleId)
        startActivity(intent)
    }

    private fun updateButtonStatus(preferences: SharedPreferences, button: Button, key: String) {
        val status = preferences.getBoolean(key, false)
        var color = resources.getColor(R.color.button, null)

        if (status) {
            color = resources.getColor(R.color.puzzle_level_complete, null)
        }
        button.setBackgroundColor(color)
    }
}
