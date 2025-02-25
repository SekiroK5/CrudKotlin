package mx.edu.utng.crudprofesional

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class PasswordEncryptor {
    private val secretKey = "your_secret_key"

    fun encrypt(password: String): String {
        val cipher = Cipher.getInstance("AES")
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedPassword = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedPassword, Base64.DEFAULT)
    }

    fun checkPassword(inputPassword: String, encryptedPassword: String): Boolean {
        val cipher = Cipher.getInstance("AES")
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedPassword = cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT))
        return inputPassword == String(decryptedPassword)
    }
}
