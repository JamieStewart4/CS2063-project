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

class HardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_hard, container, false)

        val titleTextView: TextView = view.findViewById(R.id.title_hard)
        titleTextView.text = "Hard"

        val buttonLevel13 = view.findViewById<Button>(R.id.level_13)
        buttonLevel13.setOnClickListener {
            startPuzzleMode(13)
        }

        val buttonLevel14 = view.findViewById<Button>(R.id.level_14)
        buttonLevel14.setOnClickListener {
            startPuzzleMode(14)
        }

        val buttonLevel15 = view.findViewById<Button>(R.id.level_15)
        buttonLevel15.setOnClickListener {
            startPuzzleMode(15)
        }

        val buttonLevel16 = view.findViewById<Button>(R.id.level_16)
        buttonLevel16.setOnClickListener {
            startPuzzleMode(16)
        }

        val buttonLevel17 = view.findViewById<Button>(R.id.level_17)
        buttonLevel17.setOnClickListener {
            startPuzzleMode(17)
        }

        val buttonLevel18 = view.findViewById<Button>(R.id.level_18)
        buttonLevel18.setOnClickListener {
            startPuzzleMode(18)
        }

        return view
    }

    private fun startPuzzleMode(puzzleId: Int) {
        val intent = Intent(requireContext(), PuzzleMode::class.java)
        intent.putExtra("PUZZLE_ID", puzzleId)
        startActivity(intent)
    }
}
