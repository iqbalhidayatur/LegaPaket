package com.example.legapaket

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var txtForgot: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        setupAction()
    }

    private fun initView() {
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgot = findViewById(R.id.txtForgot)
    }

    private fun setupAction() {

        btnLogin.setOnClickListener {
            validateLogin()
        }

        txtForgot.setOnClickListener {
            Toast.makeText(
                this,
                "Hubungi admin pusat untuk reset password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateLogin() {

        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (username.isEmpty()) {
            edtUsername.error = "Username wajib diisi"
            edtUsername.requestFocus()
            return
        }

        if (password.isEmpty()) {
            edtPassword.error = "Password wajib diisi"
            edtPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            edtPassword.error = "Password minimal 6 karakter"
            edtPassword.requestFocus()
            return
        }

        loginDummy(username, password)
    }

    private fun loginDummy(username: String, password: String) {

        val dummyUsername = "admin"
        val dummyPassword = "123456"

        if (username == dummyUsername && password == dummyPassword) {

            // Simpan sesi user yang login
            UserSession.login(username)

            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()

        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
        }
    }
}