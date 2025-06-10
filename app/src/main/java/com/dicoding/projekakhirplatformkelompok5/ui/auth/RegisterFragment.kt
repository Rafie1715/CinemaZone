package com.dicoding.projekakhirplatformkelompok5.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etNameRegister.text.toString().trim()
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Password dan konfirmasi password tidak cocok.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses registrasi menggunakan Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Registrasi berhasil, update nama tampilan (displayName) pengguna
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                                    // Arahkan ke halaman login
                                    (activity as? AuthActivity)?.navigateToLogin()
                                }
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Registrasi gagal: ${task.exception?.message}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
        }

        binding.tvGoToLogin.setOnClickListener {
            (activity as? AuthActivity)?.navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}