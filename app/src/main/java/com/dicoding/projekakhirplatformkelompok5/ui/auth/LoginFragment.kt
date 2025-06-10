package com.dicoding.projekakhirplatformkelompok5.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses login menggunakan Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                        (activity as? AuthActivity)?.navigateToMain()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Autentikasi gagal: ${task.exception?.message}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
        }

        binding.tvGoToRegister.setOnClickListener {
            (activity as? AuthActivity)?.navigateToRegister()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}