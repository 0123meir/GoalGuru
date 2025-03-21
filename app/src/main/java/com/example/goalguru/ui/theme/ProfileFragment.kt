package com.example.goalguru.ui.theme

import UserViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.goalguru.R
import com.example.goalguru.model.FirebaseModel
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var firebaseModel: FirebaseModel

    // Image storage related variables
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val profilePicture: ImageView = view.findViewById(R.id.profile_picture)
        val email: TextView = view.findViewById(R.id.email)
        val username: EditText = view.findViewById(R.id.username)
        val updateProfileButton: Button = view.findViewById(R.id.update_profile_button)
        val changeProfilePictureButton: Button = view.findViewById(R.id.change_profile_picture_button)

        firebaseModel = FirebaseModel(userViewModel)

        userViewModel.username.observe(viewLifecycleOwner) { retUsername ->
            username.setText(retUsername)
        }

        userViewModel.profilePicture.observe(viewLifecycleOwner) { profilePictureUrl ->
            Glide.with(this).load(profilePictureUrl).into(profilePicture)
        }

        userViewModel.email.observe(viewLifecycleOwner) { retEmail ->
            email.text = retEmail
        }

        changeProfilePictureButton.setOnClickListener {
            openFileChooser()
        }

        updateProfileButton.setOnClickListener {
            val newUsername = username.text.toString()
            firebaseModel.updateUsername(userViewModel.getCurrentUserId(), newUsername)
            uploadImage()
            userViewModel.updateUsername(newUsername)
            userViewModel.updateImage(imageUri.toString())
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    imageUri = data.data
                    profilePicture.setImageURI(imageUri)

                }
            }
        }

        val signOutButton: Button = view.findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener {
            userViewModel.logoutUser()
            // Navigate back to sign in screen or finish activity
            activity?.finish()
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageLauncher.launch(intent)
    }

    private fun uploadImage() {
        imageUri?.let {
            firebaseModel.uploadImage(requireContext(), it, userViewModel) { success, url ->
                if (success && context != null && url != null) {
                    firebaseModel.updateProfilePic(userViewModel.getCurrentUserId(), url)
                    Toast.makeText(context, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}