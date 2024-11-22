package com.example.curlingclubchampions.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.curlingclubchampions.fragments.EasyFragment
import com.example.curlingclubchampions.fragments.MediumFragment
import com.example.curlingclubchampions.fragments.HardFragment

class PuzzlePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Number of pages: Easy, Medium, Hard

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EasyFragment()   // First page: Easy
            1 -> MediumFragment() // Second page: Medium
            2 -> HardFragment()   // Third page: Hard
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
