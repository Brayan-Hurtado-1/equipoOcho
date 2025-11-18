package com.example.equipoOcho.view.fragment

import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.equipoOcho.R  // AJUSTA ESTE PACKAGE AL TUYO
import com.example.equipoOcho.utils.SessionManager



class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fingerprintView: LottieAnimationView =
            view.findViewById(R.id.lottieFingerprint)

        // Configurar el BiometricPrompt
        setupBiometricPrompt()

        // Al tocar la huella, se muestra la ventana emergente (criterio 5 y 6)
        fingerprintView.setOnClickListener {
            val biometricManager = BiometricManager.from(requireContext())
            val canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )

            if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Este dispositivo no soporta autenticación biométrica",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    // Huella correcta -> ir a Home Inventario (HU 3.0)
                    // AJUSTA el action al que tengas en tu nav_graph
                    SessionManager.setSessionActive(requireContext(), true)
                    findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    // Si el usuario toca "Cancelar", se cierra el diálogo solo,
                    // aquí solo mostramos el mensaje si quieres
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Huella incorrecta (criterio 6: no se permite el acceso)
                    Toast.makeText(
                        requireContext(),
                        "Huella no reconocida. Inténtalo de nuevo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // Ventana emergente (criterio 5)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("Cancelar") // cierra el diálogo
            .build()
    }
}
