package mx.edu.utng.crudprofesional

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
    val imageUri: String? = null
)
