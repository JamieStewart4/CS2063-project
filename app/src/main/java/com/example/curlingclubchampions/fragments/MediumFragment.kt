package com.example.curlingclubchampions.fragments

import android.content.Intent
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

        val buttonLevel7 = view.findViewById<Button>(R.id.level_7)
        buttonLevel7.setOnClickListener {
            startPuzzleMode(7)
        }

        val buttonLevel8 = view.findViewById<Button>(R.id.level_8)
        buttonLevel8.setOnClickListener {
            startPuzzleMode(8)
        }

        val buttonLevel9 = view.findViewById<Button>(R.id.level_9)
        buttonLevel9.setOnClickListener {
            startPuzzleMode(9)
        }

        val buttonLevel10 = view.findViewById<Button>(R.id.level_10)
        buttonLevel10.setOnClickListener {
            startPuzzleMode(10)
        }

        val buttonLevel11 = view.findViewById<Button>(R.id.level_11)
        buttonLevel11.setOnClickListener {
            startPuzzleMode(11)
        }

        val buttonLevel12 = view.findViewById<Button>(R.id.level_12)
        buttonLevel12.setOnClickListener {
            startPuzzleMode(12)
        }

        return view
    }

    private fun startPuzzleMode(puzzleId: Int) {
        val intent = Intent(requireContext(), PuzzleMode::class.java)
        intent.putExtra("PUZZLE_ID", puzzleId)
        startActivity(intent)
    }
}
