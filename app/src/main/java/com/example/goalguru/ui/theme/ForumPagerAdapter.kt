package com.example.goalguru.ui.theme

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ForumPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return PostsFragment.newInstance(if (position == 0) "your_posts" else "friends_posts")
    }
}