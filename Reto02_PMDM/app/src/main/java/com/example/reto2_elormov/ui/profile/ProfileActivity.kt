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
import android.widget.Button
import android.widget.ImageView
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


class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var currentPhotoUri: Uri? = null
    private lateinit var imgProfile: ImageView
    private lateinit var btnTakePhoto: Button

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView


    private val repository: ProfileRepository by lazy {
        ProfileRepository(RetrofitClient.elorServApi)
    }

    private val userId = 1

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

        imgProfile = findViewById(R.id.imgProfile)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)

        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)


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

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val response = repository.getProfile(userId)

                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        txtName.text = "${it.nombre ?: ""} ${it.apellidos ?: ""}".trim()
                        txtEmail.text = it.email ?: "Sin email"

                        // Cargar foto desde el servidor con Glide
                        val photoUrl = "${RetrofitClient.BASE_URL}api/users/${userId}/photo"
                        Glide.with(this@ProfileActivity)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground) // Foto por defecto
                            .error(R.drawable.ic_launcher_foreground) // Si falla, mostrar por defecto
                            .circleCrop() // Opcional: foto circular
                            .into(imgProfile)
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

    /*
    Sube la foto al servidor usando el ProfileRepository.
     */
    private fun uploadPhotoToServer(uri: Uri) {
        lifecycleScope.launch {
            try {
                // Convertir URI a File
                val file = uriToFile(uri)

                // Subir al servidor (ajusta el userId según tu lógica)
                val userId = 1 // TODO: Obtener del usuario logueado
                val response = repository.uploadPhoto(userId, file)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Foto subida correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            body?.message ?: "Error al subir foto",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /*
    Convierte un Uri a un File temporal en el caché de la app.
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