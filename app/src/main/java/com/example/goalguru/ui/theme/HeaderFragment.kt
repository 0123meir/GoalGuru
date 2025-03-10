package com.example.goalguru.ui.theme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.example.goalguru.R
import com.example.goalguru.LoginActivity

class HeaderFragment : Fragment() {

    // Optional callback for custom behavior
    interface HeaderCustomActionListener {
        fun onCustomExitAction(): Boolean // Return true if handled, false to use default
        fun onCustomProfileAction(): Boolean // Return true if handled, false to use default
        fun onCustomRightIconAction(iconType: RightIconType): Boolean // Return true if handled, false to use default
    }

    private var customListener: HeaderCustomActionListener? = null
    private var rightIconType: RightIconType = RightIconType.FORUM

    enum class RightIconType {
        FORUM, TODO
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up exit button
        view.findViewById<ImageView>(R.id.iv_exit).setOnClickListener {
            // Check if custom handler wants to handle this
            val customHandled = customListener?.onCustomExitAction() ?: false

            if (!customHandled) {
                // Default behavior: navigate to login
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // Set up profile photo click
        view.findViewById<ShapeableImageView>(R.id.iv_profile_photo).setOnClickListener {
            // Check if custom handler wants to handle this
            val customHandled = customListener?.onCustomProfileAction() ?: false

            if (!customHandled) {
                // navigate to profile
            }
        }

        // Set up right icon click
        val rightIcon = view.findViewById<ImageView>(R.id.iv_right_icon)
        rightIcon.setOnClickListener {
            // Check if custom handler wants to handle this
            val customHandled = customListener?.onCustomRightIconAction(rightIconType) ?: false

            if (!customHandled) {
                // Default behavior based on icon type
                when (rightIconType) {
                    RightIconType.FORUM -> {
                        // navigate to forum
                    }
                    RightIconType.TODO -> {
                        // navigate to to-do list
                    }
                }
            }
        }

        // Set the appropriate icon based on the type
        updateRightIcon()
    }

    fun setCustomActionListener(listener: HeaderCustomActionListener?) {
        this.customListener = listener
    }

    fun setRightIconType(type: RightIconType) {
        this.rightIconType = type
        updateRightIcon()
    }

    fun getRightIconType(): RightIconType {
        return rightIconType
    }

    private fun updateRightIcon() {
        view?.findViewById<ImageView>(R.id.iv_right_icon)?.let { icon ->
            when (rightIconType) {
                RightIconType.FORUM -> icon.setImageResource(R.drawable.ic_forum)
                RightIconType.TODO -> icon.setImageResource(R.drawable.to_do_list)
            }
        }
    }

    fun setProfileImage(imageUri: Uri?) {
        view?.findViewById<ShapeableImageView>(R.id.iv_profile_photo)?.let { profileImageView ->
            if (imageUri != null) {
                Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.default_profile)
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.default_profile)
            }
        }
    }
}