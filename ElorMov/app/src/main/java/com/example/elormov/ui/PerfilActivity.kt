package com.example.elormov.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.elormov.R
import com.example.elormov.ui.adapters.SpinnerAdapter
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.UserDTO
import com.example.elormov.ui.utils.LanguageUtils
import com.example.elormov.ui.utils.ThemeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private val api = RetrofitClient.elorServInterface
private lateinit var userLogin: UserDTO
private lateinit var userPerfil: UserDTO


class PerfilActivity : AppCompatActivity() {

    // ====================== GAUZAK DE LA CAMARUSKI ======================

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var currentPhotoUri: Uri? = null
    private var cambioIdioma: Boolean = false

    // Launcher para guardar foto en galeria
    private val sacarFotoGaleria =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoUri?.let {
                    Toast.makeText(this, "Imagen guardada en galeria", Toast.LENGTH_LONG).show()
                    uploadPhotoToServer(it)
                }
            } else {
                Toast.makeText(this, "Error: no se ha podido capturar la imagen", Toast.LENGTH_LONG)
                    .show()
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

        // =============== Obtener usuarios del intent ==================
        val extras: Bundle? = intent.extras
        userLogin = extras?.getSerializable("userLogin") as UserDTO
        userPerfil = extras.getSerializable("userPerfil") as UserDTO

        // ================== Setear datos del usuario ==================
        tvUserName.text = userPerfil.username
        tvTipo.text = userPerfil.tipo.name
        tvName.text = userPerfil.nombre
        tvSurnames.text = userPerfil.apellidos
        tvEmail.text = userPerfil.email
        tvPhone.text = userPerfil.telefono1
        tvPhone2.text = userPerfil.telefono2
        tvDirection.text = userPerfil.direccion
        tvDni.text = userPerfil.dni

        // ================== Cargar foto de perfil ==================
        loadProfilePhoto()

        // ================== Logica para editar foto de perfil ==================
        if(userLogin.id != userPerfil.id) {
            fabEditPhoto.visibility = View.GONE
        }
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

        // ================== Cambio de idioma ==================
        setupLanguageSpinner()

        // ================== Cambio de tema ==================
        setupThemeSpinner()

        // ================== Botón de volver atrás ==================
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (userLogin.tipo.name.uppercase() == "ALUMNO" && !cambioIdioma) {
                val intent = Intent(this, AlumnoHome::class.java)
                intent.putExtra("user", userLogin)
                startActivity(intent)
            } else if (userLogin.tipo.name.uppercase() == "PROFESOR" && !cambioIdioma) {
                val intent = Intent(this, ProfesorHome::class.java)
                intent.putExtra("user", userLogin)
                startActivity(intent)
            }
        }
    }


    // ====================== FUNCIONES Y ESAS VAINAS ======================

    /**
     * Abre la cámara para tomar una foto y guardarla en la galería
     */
    private fun camaraPfp() {
        try {
            // Configuramos los metadatos de la imagen
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "profile_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            // Insertamos la nueva imagen en el MediaStore
            currentPhotoUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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

                val response = api.uploadPhoto(userPerfil.id, body)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        Toast.makeText(
                            this@PerfilActivity,
                            "Foto subida correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Recargar la imagen en el ImageView
                        findViewById<ImageView>(R.id.ivAvatar).setImageURI(uri)
                    } else {
                        Toast.makeText(
                            this@PerfilActivity,
                            responseBody?.message ?: "Error al subir foto",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
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
                val response = api.getPfp(userPerfil.id)
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

    /**
     * Configura el spinner para seleccionar el idioma. Guarda la selección en SharedPreferences y recarga la actividad para aplicar el cambio.
     * No preguntes como funciona, son las 5 de la mañana y no tengo ni la más menor idea de lo que he hecho aquí, pero funciona y eso es lo importante
     * Es una mezcla de lo del reto pasado y la esquizofrenia que me ha producido el no dormir.
     */
    private fun setupLanguageSpinner() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val savedLanguage = LanguageUtils.getSavedLanguage(this)

        val idiomas = listOf(
            R.drawable.icono_inglish_pitinglish_foreground, // inglés posición 0
            R.drawable.icono_espanita_foreground // español posición 1
        )

        val initialPos = if (savedLanguage == "en") 0 else 1
        val spinner = findViewById<Spinner>(R.id.idiomas)
        spinner.adapter = SpinnerAdapter(this, idiomas)

        var isInitialSelection = true

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isInitialSelection) {
                    isInitialSelection = false
                    return
                }

                val selectedLang = if (position == 1) "es" else "en"

                if (savedLanguage != selectedLang) {
                    prefs.edit { putString("lang", selectedLang) }
                    LanguageUtils.setLocale(this@PerfilActivity, selectedLang)
                    cambioIdioma = true
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner.setSelection(initialPos, false)
    }

    /**
     * Configura el spinner para seleccionar el tema (claro/oscuro).
     * Guarda la selección en SharedPreferences y recarga la actividad para aplicar el cambio.
     */
    private fun setupThemeSpinner() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val savedTheme = ThemeUtils.getSavedTheme(this)

        val themes = listOf(
            R.drawable.ic_sun,  // tema claro en posición 0
            R.drawable.ic_moon  // tema oscuro en posición 1
        )

        val initialPos = if (savedTheme == ThemeUtils.THEME_LIGHT) 0 else 1
        val spinner = findViewById<Spinner>(R.id.spTema)
        spinner.adapter = SpinnerAdapter(this, themes)

        var isInitialSelection = true

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isInitialSelection) {
                    isInitialSelection = false
                    return
                }

                val selectedTheme = if (position == 0) ThemeUtils.THEME_LIGHT else ThemeUtils.THEME_DARK

                if (savedTheme != selectedTheme) {
                    prefs.edit { putString(ThemeUtils.PREF_THEME, selectedTheme) }
                    ThemeUtils.applyTheme(selectedTheme)
                    cambioIdioma = true // Marca cambio para no volver atrás
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner.setSelection(initialPos, false)
    }
}
