package mx.edu.utng.crudprofesional

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        edtEmail = findViewById(R.id.emailInput)
        edtPassword = findViewById(R.id.passwordInput)
        btnLogin = findViewById(R.id.loginButton)
        btnRegister = findViewById(R.id.registerButton)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Evento para el botón de login
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val cursor = dbHelper.getUser(email, password)

                // Verificar si el cursor contiene al menos un registro
                if (cursor.moveToFirst()) {
                    val role = dbHelper.getUserRole(cursor)
                    if (role != null) {
                        when (role) {
                            "Superadministrador" -> {
                                startActivity(Intent(this, AdminActivity::class.java))
                            }
                            "Administrador" -> {
                                startActivity(Intent(this, AdminActivity::class.java))
                            }
                            "Cliente" -> {
                                startActivity(Intent(this, UserActivity::class.java))
                            }
                            else -> {
                                Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Rol no encontrado en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }

                cursor.close()
            } else {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento para el botón de registro
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}


