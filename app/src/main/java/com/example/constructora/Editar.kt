package com.example.constructora

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class Editar : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var btnGuardar: Button
    private lateinit var imagenVista: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var etPresupuesto: EditText
    private lateinit var etSolicitante: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var spEstado: Spinner
    private lateinit var spRegion: Spinner
    private lateinit var spFechai: TextView
    private lateinit var spFechat: TextView

    private var imagenUri: Uri? = null
    private var selectedFechaInicio: String? = null
    private var selectedFechaTermino: String? = null
    private var proyectoId: String? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        inicializarVistas()
        proyectoId = intent.getStringExtra("codigo")
        proyectoId?.let { cargarDatosProyecto(it) }
        configurarListeners()
    }

    private fun inicializarVistas() {
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnGuardar = findViewById(R.id.btnGuardar)
        imagenVista = findViewById(R.id.imagenVista)
        etNombre = findViewById(R.id.etNombre)
        etUbicacion = findViewById(R.id.etUbicacion)
        etPresupuesto = findViewById(R.id.etPresupuesto)
        etSolicitante = findViewById(R.id.etSolicitante)
        etDescripcion = findViewById(R.id.etDescripcion)
        spEstado = findViewById(R.id.spEstado)
        spRegion = findViewById(R.id.spRegion)
        spFechai = findViewById(R.id.spFechai)
        spFechat = findViewById(R.id.spFechat)

        val estados = arrayOf("EN PROCESO", "NO INICIADO", "SUSPENDIDO", "FINALIZADO")
        val regiones = arrayOf("ARICA Y PARINACOTA", "TARAPACA", "ANTOFAGASTA", "COQUIMBO", "VALPARAISO", "METROPOLITANA", "O'HIGGINS", "MAULE", "ÑUBLE", "BIO BIO", "LA ARAUCANIA", "LOS LAGOS", "AYSEN", "MAGALLANES")

        spEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spRegion.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, regiones)

        spFechai.setOnClickListener { mostrarDatePicker { fecha ->
            spFechai.text = fecha
            selectedFechaInicio = fecha
        } }

        spFechat.setOnClickListener { mostrarDatePicker { fecha ->
            spFechat.text = fecha
            selectedFechaTermino = fecha
        } }
    }

    private fun configurarListeners() {
        btnSeleccionar.setOnClickListener { seleccionarImagen() }
        btnGuardar.setOnClickListener { guardarCambios() }
    }

    private fun cargarDatosProyecto(id: String) {
        db.collection("Proyectos").document(id).get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    etNombre.setText(documento.getString("nombre"))
                    etUbicacion.setText(documento.getString("ubicacion"))
                    etPresupuesto.setText(documento.getString("presupuesto"))
                    etSolicitante.setText(documento.getString("solicitante"))
                    etDescripcion.setText(documento.getString("descripcion"))

                    spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(documento.getString("estado")))
                    spRegion.setSelection((spRegion.adapter as ArrayAdapter<String>).getPosition(documento.getString("region")))

                    spFechai.text = documento.getString("fecha_i")
                    spFechat.text = documento.getString("fecha_t")

                    val imagenUrl = documento.getString("docUrl")
                    imagenUrl?.let {
                        Glide.with(this).load(it).into(imagenVista)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            imagenVista.setImageURI(imagenUri)
        }
    }

    private fun guardarCambios() {
        if (!validarCampos()) return

        subirImagen { imagenUrl ->
            val proyectoData = hashMapOf(
                "nombre" to etNombre.text.toString().trim(),
                "ubicacion" to etUbicacion.text.toString().trim(),
                "presupuesto" to etPresupuesto.text.toString().trim(),
                "solicitante" to etSolicitante.text.toString().trim(),
                "descripcion" to etDescripcion.text.toString().trim(),
                "estado" to spEstado.selectedItem.toString(),
                "region" to spRegion.selectedItem.toString(),
                "fecha_i" to selectedFechaInicio,
                "fecha_t" to selectedFechaTermino,
                "docUrl" to imagenUrl
            )

            proyectoId?.let { id ->
                db.collection("Proyectos").document(id)
                    .set(proyectoData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Proyecto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar el proyecto", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (etNombre.text.toString().trim().isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            return false
        }
        if (etUbicacion.text.toString().trim().isEmpty()) {
            etUbicacion.error = "La ubicación es obligatoria"
            return false
        }
        if (etDescripcion.text.toString().trim().isEmpty()) {
            etDescripcion.error = "La descripción es obligatoria"
            return false
        }
        if (spRegion.selectedItemPosition == 0) {
            Toast.makeText(this, "Debe seleccionar una región", Toast.LENGTH_SHORT).show()
            return false
        }
        if (spEstado.selectedItemPosition == 0) {
            Toast.makeText(this, "Debe seleccionar un estado", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedFechaInicio == null || selectedFechaTermino == null) {
            Toast.makeText(this, "Debe seleccionar las fechas", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun subirImagen(callback: (String) -> Unit) {
        imagenUri?.let { uri ->
            val nombreImagen = "proyecto_${proyectoId}_${System.currentTimeMillis()}"
            val referenciaImagen = storage.reference.child("imagenes_proyectos/$nombreImagen")

            referenciaImagen.putFile(uri)
                .addOnSuccessListener {
                    referenciaImagen.downloadUrl.addOnSuccessListener { downloadUrl ->
                        callback(downloadUrl.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "error al subir imagen", Toast.LENGTH_SHORT).show()
                    callback("")
                }
        } ?: run {
            callback("")
        }
    }

    private fun mostrarDatePicker(callback: (String) -> Unit) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val fechaSeleccionada = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            callback(fechaSeleccionada)
        }, year, month, day).show()
    }


    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
