package com.example.equipoOcho.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.equipoOcho.R
import com.example.equipoOcho.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBiometricPrompt()

        // Criterio 5: al tocar la huella, mostrar ventana de autenticación
        binding.lottieFingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // Se cierra el diálogo o hay error grave
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    // Criterio 6: huella correcta → ir a Home Inventario (HU 3.0)
                    findNavController()
                        .navigate(R.id.action_loginFragment_to_homeInventoryFragment)
                }

                override fun onAuthenticationFailed() {
                    // Criterio 6: huella incorrecta → mensaje, sin acceso
                    Toast.makeText(
                        requireContext(),
                        "Huella no reconocida. Intenta de nuevo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // Criterio 5: título, subtítulo y botón Cancelar
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("CANCEL")   // cierra la ventana automáticamente
            .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
