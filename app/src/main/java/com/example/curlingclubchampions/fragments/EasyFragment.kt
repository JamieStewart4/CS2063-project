package com.example.curlingclubchampions.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.curlingclubchampions.PuzzleMode
import com.example.curlingclubchampions.R

class EasyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_easy, container, false)

        val buttonLevel1 = view.findViewById<Button>(R.id.level_1)
        buttonLevel1.setOnClickListener {
            startPuzzleMode(1)
        }

        val buttonLevel2 = view.findViewById<Button>(R.id.level_2)
        buttonLevel2.setOnClickListener {
            startPuzzleMode(2)
        }

        val buttonLevel3 = view.findViewById<Button>(R.id.level_3)
        buttonLevel3.setOnClickListener {
            startPuzzleMode(3)
        }

        val buttonLevel4 = view.findViewById<Button>(R.id.level_4)
        buttonLevel4.setOnClickListener {
            startPuzzleMode(4)
        }

        val buttonLevel5 = view.findViewById<Button>(R.id.level_5)
        buttonLevel5.setOnClickListener {
            startPuzzleMode(5)
        }

        val buttonLevel6 = view.findViewById<Button>(R.id.level_6)
        buttonLevel6.setOnClickListener {
            startPuzzleMode(6)
        }

        return view
    }

    private fun startPuzzleMode(puzzleId: Int) {
        val intent = Intent(requireContext(), PuzzleMode::class.java)
        intent.putExtra("PUZZLE_ID", puzzleId)
        startActivity(intent)
    }
}
