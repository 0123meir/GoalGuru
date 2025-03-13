package com.example.goalguru.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.goalguru.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ForumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_forum layout
        return inflater.inflate(R.layout.fragment_forum, container, false).apply {
            val viewPager: ViewPager2 = findViewById(R.id.viewPager)
            val tabLayout: TabLayout = findViewById(R.id.tabLayout)

            val adapter = ForumPagerAdapter(this@ForumFragment)
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = if (position == 0) "Your Posts" else "Friends"
            }.attach()
        }
    }
}