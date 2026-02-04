package com.example.elormov.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.elormov.R
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.UserDTO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private val api = RetrofitClient.elorServInterface
private lateinit var user: UserDTO


class PerfilActivity : AppCompatActivity() {

    // ====================== GAUZAK DE LA CAMARUSKI ======================

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var currentPhotoUri: Uri? = null

    // Launcher para guardar foto en galeria
    private val sacarFotoGaleria = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                Toast.makeText(this, "Imagen guardada en galeria", Toast.LENGTH_LONG).show()
                uploadPhotoToServer(it)
            }
        } else {
            Toast.makeText(this, "Error: no se ha podido capturar la imagen", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // ================== Inicializar variables ==================
        val imgView = findViewById<ImageView>(R.id.ivAvatar)
        val tvUserName = findViewById<TextView>(R.id.tvUsername)
        val tvTipo = findViewById<TextView>(R.id.tvRole)
        val tvName = findViewById<TextView>(R.id.tvNombre)
        val tvSurnames = findViewById<TextView>(R.id.tvApellidos)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvPhone2 = findViewById<TextView>(R.id.tvPhone2)
        val tvDni = findViewById<TextView>(R.id.tvDni)
        val tvDirection = findViewById<TextView>(R.id.tvDireccion)
        val fabEditPhoto = findViewById<FloatingActionButton>(R.id.fabEditPhoto)

        // =============== Obtener usuario ==================
        val extras: Bundle? = intent.extras
        user = extras?.getSerializable("user") as UserDTO
        Toast.makeText(this, "Perfil de ${user.nombre}", Toast.LENGTH_SHORT).show()

        // ================== Setear datos del usuario ==================
        tvUserName.text = user.username
        tvTipo.text = user.tipo.name
        tvName.text = user.nombre
        tvSurnames.text = user.apellidos
        tvEmail.text = user.email
        tvPhone.text = user.telefono1
        tvPhone2.text = user.telefono2
        tvDirection.text = user.direccion
        tvDni.text = user.dni

        // ================== Cargar foto de perfil ==================
        loadProfilePhoto()

        // ================== Logica para editar foto de perfil ==================
        fabEditPhoto.setOnClickListener {
            // Verificamos el permiso de cámara
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                camaraPfp()
            } else { // Solicitamos el permiso de cámara
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }

        }
    }

    private fun camaraPfp() {
        try {
            // Configuramos los metadatos de la imagen
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "profile_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            // Insertamos la nueva imagen en el MediaStore
            currentPhotoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            currentPhotoUri?.let { sacarFotoGaleria.launch(it) }
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Error: no se puede abrir la cámara",
                Toast.LENGTH_LONG
            ).show()
            e.stackTrace
        }
    }

    /**
     * Sube la foto al servidor
     */
    private fun uploadPhotoToServer(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = uriToFile(uri)

                // Crear el RequestBody y MultipartBody.Part
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                val response = api.uploadPhoto(user.id, body)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        Toast.makeText(this@PerfilActivity, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                        // Recargar la imagen en el ImageView
                        findViewById<ImageView>(R.id.ivAvatar).setImageURI(uri)
                    } else {
                        Toast.makeText(this@PerfilActivity, responseBody?.message ?: "Error al subir foto", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Error HTTP ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PerfilActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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

    /**
     * Carga la foto de perfil desde el servidor y la muestra en el ImageView. Si no hay foto, muestra una imagen por defecto.
     */
    private fun loadProfilePhoto() {
        val imageView = findViewById<ImageView>(R.id.ivAvatar)

        lifecycleScope.launch {
            try {
                val response = api.getPfp(user.id)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())

                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            imageView.setImageResource(R.drawable.ic_profile)
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_profile)
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_profile)
                }
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.ic_profile)
            }
        }
    }


}