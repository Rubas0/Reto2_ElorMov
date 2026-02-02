package com.example.reto2_elormov.ui.profile

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.repository.ProfileRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.example.reto2_elormov.utils.Prefs
import java.util.Locale
import kotlin.text.lowercase


/**
 * Activity para mostrar y editar el perfil del usuario. Permite ver información básica y específica
 * según el tipo de usuario (alumno o profesor), así como capturar y subir una foto de perfil.
 */
class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var currentPhotoUri: Uri? = null

    // Vistas comunes
    private lateinit var imgProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtUsername: TextView
    private lateinit var txtEmail: TextView
    private lateinit var btnTakePhoto: Button

    // Vistas específicas ALUMNO
    private lateinit var layoutAlumnoInfo: LinearLayout
    private lateinit var txtCiclo: TextView
    private lateinit var txtCurso: TextView
    private lateinit var txtDual: TextView

    // Vistas específicas PROFESOR
    private lateinit var layoutProfesorInfo: LinearLayout
    private lateinit var txtDepartamento: TextView
    private lateinit var txtTutorDe: TextView


    private val repository: ProfileRepository by lazy {
        ProfileRepository(RetrofitClient.elorServApi)
    }

    // Obtener userId del usuario logueado desde Prefs
    private val prefs: Prefs by lazy { Prefs(this) }
    private val userId: Int by lazy { prefs.userId }

    // Launcher para capturar foto y guardarla en galería. Un launcher se define como una propiedad.
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                // Mostrar la foto capturada
                imgProfile.setImageURI(uri)
                Toast.makeText(this, "Foto capturada correctamente", Toast.LENGTH_SHORT).show()

                // Subir al servidor
                uploadPhotoToServer(uri)
            }
        } else {
            Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar vistas comunes
        imgProfile = findViewById(R.id.imgProfile)
        txtName = findViewById(R.id.txtName)
        txtUsername = findViewById(R.id.txtUsername)
        txtEmail = findViewById(R.id.txtEmail)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)

        // Inicializar vistas específicas ALUMNO
        layoutAlumnoInfo = findViewById(R.id.layoutAlumnoInfo)
        txtCiclo = findViewById(R.id.txtCiclo)
        txtCurso = findViewById(R.id.txtCurso)
        txtDual = findViewById(R.id.txtDual)

        // Inicializar vistas específicas PROFESOR
        layoutProfesorInfo = findViewById(R.id.layoutProfesorInfo)
        txtDepartamento = findViewById(R.id.txtDepartamento)
        txtTutorDe = findViewById(R.id.txtTutorDe)

        // Verificar si el usuario está logueado
        if (userId == -1) {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar perfil del servidor
        loadProfile()


        btnTakePhoto.setOnClickListener {
            // Verificar permiso de cámara
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    /**
    Carga el perfil del usuario desde el servidor y actualiza la UI.
     */
    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val response = repository.getProfile(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        txtName.text = "${it.nombre ?: ""} ${it.apellidos ?: ""}".trim()
                        txtUsername.text = "@${it.username ?: ""}"
                        txtEmail.text = it.email ?: "Sin email"

                        val photoUrl = "${RetrofitClient.BASE_URL}/api/users/${userId}/photo"
                        Glide.with(this@ProfileActivity)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imgProfile)

                        val tipoId = it.tipoId?.id ?: 0
                        when (tipoId) {
                            1 -> {
                                layoutAlumnoInfo.visibility = View.VISIBLE
                                layoutProfesorInfo.visibility = View.GONE
                                txtCiclo.text = "Ciclo: ${it.ciclo ?: "N/A"}"
                                txtCurso.text = "Curso: ${it.curso ?: "N/A"}"
                                txtDual.text = "Dual Intensiva: ${if (it.dualIntensiva == true) "Sí" else "No"}"
                            }
                            3 -> {
                                layoutAlumnoInfo.visibility = View.GONE
                                layoutProfesorInfo.visibility = View.VISIBLE
                                txtDepartamento.text = "Departamento: ${it.departamento ?: "N/A"}"
                                txtTutorDe.text = "Tutor de: ${it.tutorDe ?: "N/A"}"
                            }
                            else -> {
                                layoutAlumnoInfo.visibility = View.GONE
                                layoutProfesorInfo.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error al cargar perfil", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun openCamera() {
        try {
            // Configurar metadatos de la imagen
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "profile_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            // Insertar nueva imagen en MediaStore
            currentPhotoUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            // Lanzar cámara
            currentPhotoUri?.let { takePictureLauncher.launch(it) }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: no se puede abrir la cámara", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    /**
    Sube la foto al servidor usando el ProfileRepository.
     */
    private fun uploadPhotoToServer(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = uriToFile(uri)

                // Usa el userId almacenado en Prefs
                val response = repository.uploadPhoto(userId, file)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Toast.makeText(this@ProfileActivity, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, body?.message ?: "Error al subir foto", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error HTTP ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
    Convierte un Uri a un File temporal en el caché de la app. Lo usamos para subir la foto al servidor.
     */
    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "profile_photo.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}