package com.example.goalguru.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.goalguru.R
import com.example.goalguru.databinding.FragmentHeaderBinding

class HeaderFragment : Fragment() {

    private var _binding: FragmentHeaderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Navigate to profile when profile photo is clicked
        binding.ivProfilePhoto.setOnClickListener {
            // Navigate to profile regardless of current fragment
            when(findNavController().currentDestination?.id) {
                R.id.todoListFragment -> findNavController().navigate(R.id.action_todo_to_profile)
                R.id.forumFragment -> findNavController().navigate(R.id.action_forum_to_profile)
                // If already on profile, do nothing
            }
        }

        // Navigate to forum when forum icon is clicked
        binding.ivForum.setOnClickListener {
            // Navigate to forum regardless of current fragment
            when(findNavController().currentDestination?.id) {
                R.id.todoListFragment -> findNavController().navigate(R.id.action_todo_to_forum)
                R.id.profileFragment -> findNavController().navigate(R.id.action_profile_to_forum)
                // If already on forum, do nothing
            }
        }

        // Navigate to todo list when todo list button is clicked
        binding.ivTodoList.setOnClickListener {
            // Navigate to todo list regardless of current fragment
            when(findNavController().currentDestination?.id) {
                R.id.forumFragment -> findNavController().navigate(R.id.action_forum_to_todo)
                R.id.profileFragment -> findNavController().navigate(R.id.action_profile_to_todo)
                // If already on todo list, do nothing
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}