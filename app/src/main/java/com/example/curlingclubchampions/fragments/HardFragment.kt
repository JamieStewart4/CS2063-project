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

class HardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_hard, container, false)

        val titleTextView: TextView = view.findViewById(R.id.title_hard)
        titleTextView.text = "Hard"

        val preferences = requireContext().getSharedPreferences("level_status", Context.MODE_PRIVATE)

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

        val buttonLevel13 = view.findViewById<Button>(R.id.level_13)
        buttonLevel13.setOnClickListener {
            startPuzzleMode(13)
        }
        updateButtonStatus(preferences, buttonLevel13, 13.toString())

        val buttonLevel14 = view.findViewById<Button>(R.id.level_14)
        buttonLevel14.setOnClickListener {
            startPuzzleMode(14)
        }
        updateButtonStatus(preferences, buttonLevel14, 14.toString())

        val buttonLevel15 = view.findViewById<Button>(R.id.level_15)
        buttonLevel15.setOnClickListener {
            startPuzzleMode(15)
        }
        updateButtonStatus(preferences, buttonLevel15, 15.toString())

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
