package mx.edu.utng.crudprofesional

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class EditUserActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var imgProfile: ImageView
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtPassword = findViewById(R.id.edtPassword)
        imgProfile = findViewById(R.id.imgProfile)
        btnSave = findViewById(R.id.btnSave)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        // Cargar datos del usuario
        loadUserData(userId)

        // Guardar cambios
        btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData(userId: Int) {
        val user = dbHelper.getUserById(userId)
        if (user != null) {
            edtName.setText(user.name)
            edtEmail.setText(user.email)
            edtPhone.setText(user.phone)
            edtPassword.setText(user.password)
            Glide.with(this).load(user.imageUri).into(imgProfile)
        }
    }

    private fun saveUserData() {
        val name = edtName.text.toString()
        val email = edtEmail.text.toString()
        val phone = edtPhone.text.toString()
        val password = edtPassword.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
            val success = dbHelper.updateUser(userId, name, email, phone, password)
            if (success) {
                finish() // Cerrar la actividad y regresar
            } else {
                // Mostrar error
            }
        }
    }
}
