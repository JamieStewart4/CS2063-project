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

        val buttonLevel16 = view.findViewById<Button>(R.id.level_16)
        buttonLevel16.setOnClickListener {
            startPuzzleMode(16)
        }
        updateButtonStatus(preferences, buttonLevel16, 16.toString())

        val buttonLevel17 = view.findViewById<Button>(R.id.level_17)
        buttonLevel17.setOnClickListener {
            startPuzzleMode(17)
        }
        updateButtonStatus(preferences, buttonLevel17, 17.toString())

        val buttonLevel18 = view.findViewById<Button>(R.id.level_18)
        buttonLevel18.setOnClickListener {
            startPuzzleMode(18)
        }
        updateButtonStatus(preferences, buttonLevel18, 18.toString())

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
