package com.example.equipoocho.ui.login


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.equipoocho.databinding.ActivityLoginBinding
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.content.Intent
import com.example.equipoocho.ui.home.HomeActivity

// Pantalla de login que usa autenticación biométrica (huella dactilar)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)


// Si ya hay sesión guardada, saltar a Home
        // Si el usuario ya tiene sesión guardada, se salta el login
        if (session.isLogged()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // Si no está logueado, espera que toque la animación de huella
        binding.fingerprintAnim.setOnClickListener { showBiometric() }
    }


    // Muestra el diálogo de autenticación biométrica
    private fun showBiometric() {
        val executor = ContextCompat.getMainExecutor(this)

        // Configura el callback que se ejecuta según el resultado de la autenticación
        val prompt = BiometricPrompt(this, executor, object: BiometricPrompt.AuthenticationCallback() {

            // Si la autenticación con huella es exitosa
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                session.setLogged(true)
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish() // Cierra la actividad actual
            }
        })

        // Configura el texto del cuadro de autenticación
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(com.example.equipoocho.R.string.login_title))
            .setSubtitle(getString(com.example.equipoocho.R.string.login_sub))
            .setNegativeButtonText(getString(com.example.equipoocho.R.string.cancel))
            .build()

        // Muestra el diálogo biométrico
        prompt.authenticate(info)
    }
}