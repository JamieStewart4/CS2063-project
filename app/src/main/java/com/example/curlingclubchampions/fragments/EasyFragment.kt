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

class EasyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_easy, container, false)

        val titleTextView: TextView = view.findViewById(R.id.title_easy)
        titleTextView.text = "Easy"

        val preferences = requireContext().getSharedPreferences("level_status", Context.MODE_PRIVATE)

        val buttonLevel1 = view.findViewById<Button>(R.id.level_1)
        buttonLevel1.setOnClickListener {
            startPuzzleMode(1)
        }
        updateButtonStatus(preferences, buttonLevel1, 1.toString())

        val buttonLevel2 = view.findViewById<Button>(R.id.level_2)
        buttonLevel2.setOnClickListener {
            startPuzzleMode(2)
        }
        updateButtonStatus(preferences, buttonLevel2, 2.toString())

        val buttonLevel3 = view.findViewById<Button>(R.id.level_3)
        buttonLevel3.setOnClickListener {
            startPuzzleMode(3)
        }
        updateButtonStatus(preferences, buttonLevel3, 3.toString())

        val buttonLevel4 = view.findViewById<Button>(R.id.level_4)
        buttonLevel4.setOnClickListener {
            startPuzzleMode(4)
        }
        updateButtonStatus(preferences, buttonLevel4, 4.toString())

        val buttonLevel5 = view.findViewById<Button>(R.id.level_5)
        buttonLevel5.setOnClickListener {
            startPuzzleMode(5)
        }
        updateButtonStatus(preferences, buttonLevel5, 5.toString())

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
