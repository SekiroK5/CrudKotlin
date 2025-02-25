package mx.edu.utng.crudprofesional

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnUpdate: Button
    private var userId: Int = -1  // ID del usuario a editar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        dbHelper = DatabaseHelper(this)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        btnUpdate = findViewById(R.id.btnUpdate)
        var btnSalir = findViewById<Button>(R.id.btnExit)
        // Obtener el userId desde el Intent
        userId = intent.getIntExtra("USER_ID", -1)

        // Verificar si el userId es válido
        if (userId != -1) {
            loadUserData()
        } else {
            Toast.makeText(this, "Error: ID de usuario no válido", Toast.LENGTH_SHORT).show()
        }

        btnSalir.setOnClickListener {
            finish()
        }

        // Actualización de los datos del usuario
        btnUpdate.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val phone = edtPhone.text.toString()

            if (dbHelper.updateUser(userId, name, email, phone)) {
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserData() {
        // Cargar los datos del usuario desde la base de datos
        val user = dbHelper.getUserById(userId)
        user?.let {
            edtName.setText(it.name)
            edtEmail.setText(it.email)
            edtPhone.setText(it.phone)
        }
    }
}
