package com.example.goalguru.ui.theme

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.goalguru.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ForumFragment : Fragment() {

    private lateinit var dialogHandler: PostDialogHandler

    private fun findPostsFragment(type: String): PostsFragment? {
        val fragments = childFragmentManager.fragments
        return fragments.filterIsInstance<PostsFragment>()
            .find { it.getPostType() == type }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val fabAddPost: FloatingActionButton = view.findViewById(R.id.fab_add_post)

        val adapter = ForumPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Your Posts" else "Explore"
        }.attach()

        dialogHandler = PostDialogHandler(requireContext())

        fabAddPost.setOnClickListener {
            val yourPostsFragment = findPostsFragment("your_posts")
            yourPostsFragment?.addNewPost()
        }

        return view
    }
}