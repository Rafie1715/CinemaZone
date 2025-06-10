package com.dicoding.projekakhirplatformkelompok5.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentProfileBinding
import com.dicoding.projekakhirplatformkelompok5.ui.auth.AuthActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            showEditNameDialog()
        }

        binding.btnOrderHistory.setOnClickListener {
            startActivity(Intent(activity, OrderHistoryActivity::class.java))
        }

        binding.btnAboutApp.setOnClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvProfileName.text = currentUser.displayName ?: "Nama Belum Diatur"
            binding.tvProfileEmail.text = currentUser.email
        }
    }

    private fun showEditNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ubah Nama")

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(48, 0, 48, 0)
            layoutParams = lp
        }

        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            hint = "Nama Baru"
            setText(binding.tvProfileName.text)
        }

        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                saveNewNameToFirebase(newName)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun saveNewNameToFirebase(newName: String) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = newName
        }
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Nama berhasil diperbarui", Toast.LENGTH_SHORT).show()
                binding.tvProfileName.text = newName
            } else {
                Toast.makeText(requireContext(), "Gagal memperbarui nama.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ -> logoutUser() }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}