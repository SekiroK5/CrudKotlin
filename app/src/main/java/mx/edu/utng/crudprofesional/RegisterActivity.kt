package mx.edu.utng.crudprofesional

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtName: EditText
    private lateinit var edtBirthdate: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)
        edtName = findViewById(R.id.nameInput)
        edtBirthdate = findViewById(R.id.birthdateInput)
        edtEmail = findViewById(R.id.emailInput)
        edtPhone = findViewById(R.id.phoneInput)
        edtPassword = findViewById(R.id.passwordInput)
        edtConfirmPassword = findViewById(R.id.confirmPasswordInput)
        btnRegister = findViewById(R.id.registerButton)
        btnExit = findViewById(R.id.exitButton)

        btnRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val birthdate = edtBirthdate.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            if (name.isEmpty() || birthdate.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "⚠️ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "⚠️ Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val success = dbHelper.registerUser(name, birthdate, email, phone, password)
                if (success) {
                    Toast.makeText(this, "✅ Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "❌ Error al registrar usuario", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error al registrar usuario", e)
                Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        btnExit.setOnClickListener {
            finish()
        }
    }
}
