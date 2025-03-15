package com.example.goalguru.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.goalguru.databinding.NewEditTaskLayoutBinding
import com.example.goalguru.model.Model

class NewGoalFragment : Fragment() {
    private var _binding: NewEditTaskLayoutBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewEditTaskLayoutBinding.inflate(inflater, container, false)
        return binding?.root ?: throw RuntimeException("Invalid layout")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val existingGoals = Model.shared.tasks.map { it.goal }.distinct()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingGoals)
        binding?.taskGoalInput?.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}