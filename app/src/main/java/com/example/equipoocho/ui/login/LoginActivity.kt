package com.example.equipoocho.ui.login


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.equipoocho.databinding.ActivityLoginBinding
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.content.Intent
import com.example.equipoocho.ui.home.HomeActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)


// Si ya hay sesión guardada, saltar a Home
        if (session.isLogged()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }


        binding.fingerprintAnim.setOnClickListener { showBiometric() }
    }


    private fun showBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                session.setLogged(true)
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        })


        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(com.example.equipoocho.R.string.login_title))
            .setSubtitle(getString(com.example.equipoocho.R.string.login_sub))
            .setNegativeButtonText(getString(com.example.equipoocho.R.string.cancel))
            .build()
        prompt.authenticate(info)
    }
}