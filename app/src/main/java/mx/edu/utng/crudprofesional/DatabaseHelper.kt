package mx.edu.utng.crudprofesional

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                birthdate DATE,
                email TEXT UNIQUE,
                phone TEXT,
                password TEXT,
                age INTEGER,
                imageUri TEXT,
                role TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // Encripta la contraseña usando SHA-256 y la convierte en Base64
    fun encryptPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    // Registra un nuevo usuario con asignación automática de roles
    fun registerUser(name: String, birthdate: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase

        // Verificar si es el primer usuario para asignar el rol de Superadministrador
        val role = if (getUserCount() == 0) "Superadministrador" else "Cliente"
        val imageUri = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"

        val values = ContentValues().apply {
            put("name", name)
            put("birthdate", birthdate)
            put("email", email)
            put("phone", phone)
            put("password", encryptPassword(password))
            put("role", role)  // Asignación automática del rol
            put("imageUri", imageUri) // Imagen por defecto
        }

        return try {
            val result = db.insert("users", null, values)
            if (result == -1L) {
                println("❌ Error: No se pudo insertar el usuario")
                false
            } else {
                println("✅ Usuario registrado correctamente con ID: $result")
                true
            }
        } catch (e: Exception) {
            println("⚠️ Error al registrar usuario: ${e.message}")
            false
        } finally {
            db.close()
        }
    }


    // Actualiza la imagen del usuario
    fun updateUserImage(userId: Int, imageUri: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("imageUri", imageUri)
        }
        val result = db.update("users", values, "id = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    // Obtiene un usuario por su email y contraseña encriptada
    fun getUser(email: String, password: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", arrayOf(email, encryptPassword(password)))
    }

    // Obtiene un usuario por su ID
    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            "users",
            null,
            "id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("id")
            val nameIndex = cursor.getColumnIndex("name")
            val emailIndex = cursor.getColumnIndex("email")
            val phoneIndex = cursor.getColumnIndex("phone")
            val passwordIndex = cursor.getColumnIndex("password")
            val imageUriIndex = cursor.getColumnIndex("imageUri")

            if (idIndex != -1 && nameIndex != -1 && emailIndex != -1 && phoneIndex != -1 && passwordIndex != -1 && imageUriIndex != -1) {
                val user = User(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(phoneIndex),
                    cursor.getString(passwordIndex),
                    cursor.getString(imageUriIndex)
                )
                cursor.close()
                db.close()
                return user
            }
        }

        cursor?.close()
        db.close()
        return null
    }


    fun updateUser(userId: Int, name: String, email: String, phone: String, password: String? = null): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("phone", phone)
            password?.let { put("password", encryptPassword(it)) }
        }

        val rowsUpdated = db.update(
            "users",
            contentValues,
            "id = ?",
            arrayOf(userId.toString())
        )

        db.close()
        return rowsUpdated > 0
    }


    fun deleteUser(userId: Int) {
        val db = this.readableDatabase
        // Elimina el registro de la tabla 'users' donde el ID coincida
        db.delete("users", "id = ?", arrayOf(userId.toString()))
        db.close()
    }

    // Obtiene el siguiente ID disponible para un nuevo usuario
    fun getNextUserId(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX(id) FROM users"
        val cursor = db.rawQuery(query, null)
        var nextId = 1 // Si no hay usuarios, el primer ID será 1
        if (cursor.moveToFirst()) {
            nextId = cursor.getInt(0) + 1
        }
        cursor.close()
        return nextId
    }

    // Obtiene el rol de un usuario
    fun getUserRole(cursor: Cursor): String? {
        val roleIndex = cursor.getColumnIndex("role")
        return if (roleIndex != -1) cursor.getString(roleIndex) else null
    }

    // Obtiene todos los clientes (Para la vista de administrador)
    fun getAllClients(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE role = 'Cliente'", null)
    }

    // Obtiene todos los usuarios (Para el administrador principal)
    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
                val roles = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                val user = User(id, name, email, phone, imageUri, roles)
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return userList
    }


    // Actualiza el rol de un usuario
    fun updateUserRole(userId: Int, newRole: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("role", newRole)
        }
        val result = db.update("users", values, "id = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    // Actualiza los datos del usuario (excepto el rol y la contraseña)

    // Actualiza la contraseña del usuario
    fun updatePassword(userId: Int, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("password", encryptPassword(newPassword))
        }
        val result = db.update("users", values, "id = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    // Obtiene el número de usuarios en la base de datos
    private fun getUserCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
        return if (cursor.moveToFirst()) cursor.getInt(0) else 0
    }

    // Obtiene un usuario por su email
    fun getUserByEmail(email: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))
    }

    companion object {
        private const val DATABASE_NAME = "userapp.db"
        private const val DATABASE_VERSION = 1
    }
}
