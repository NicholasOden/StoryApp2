package com.example.picodiploma.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if token exists
        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString()
            val password = binding.editTextPasswordLogin.text.toString()

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiServiceHelper = ApiServiceHelper(null)
                val loginResponse = apiServiceHelper.login(email, password)
                if (!loginResponse.error) {
                    val token = loginResponse.loginResult?.token
                    val userId = loginResponse.loginResult?.userId
                    val name = loginResponse.loginResult?.name

                    // Save the token and user info to shared preferences
                    val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", token)
                    editor.putString("userId", userId)
                    editor.putString("name", name)
                    editor.apply()

                    // Start the main activity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Login failed: ${loginResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
