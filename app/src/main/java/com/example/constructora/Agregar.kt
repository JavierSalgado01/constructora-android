package com.example.constructora

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class Agregar : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var btnSubir: Button
    private lateinit var btnGuardar: Button
    private lateinit var imagenVista: ImageView
    private lateinit var etCodigo: EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar)

        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnSubir = findViewById(R.id.btnSubir)
        btnGuardar = findViewById(R.id.btnGuardar)
        imagenVista = findViewById(R.id.imagenVista)
        etCodigo = findViewById(R.id.etCodigo)
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
        val regiones = arrayOf("ARICA Y PARINACOTA", "TARAPACA", "ANTOFAGASTA", "COQUIMBO", "VALPARAISO", "METROPOLITANA", "O'HIGGINS", "MAULE", "ÑUBLE", "BIO BIO", "LA ARAUCANIA", "LOS LAGOS", "AYSEN DEL GENERAL CARLOS IBAÑEZ DEL CAMPO", "MAGALLANES Y LA ANTARTICA CHILENA")

        spEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spRegion.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, regiones)
        spFechai.setOnClickListener { mostrarDatePicker(spFechai) }
        spFechat.setOnClickListener { mostrarDatePicker(spFechat) }

        btnSeleccionar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        btnSubir.setOnClickListener {
            if (imagenUri == null) {
                Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Imagen cargada localmente.", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            guardarProyecto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            imagenVista.setImageURI(imagenUri) // muestra imagen a subir
        }
    }

    private fun mostrarDatePicker(textView: TextView) {
        val calendario = Calendar.getInstance()
        val y = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // formatiar fecha seleccionada
                val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                textView.text = fechaSeleccionada
            },
            y, mes, dia
        )
        datePickerDialog.show()
    }

    private fun guardarProyecto() {
        val codigo = etCodigo.text.toString()
        val nombre = etNombre.text.toString()
        val ubicacion = etUbicacion.text.toString()
        val presupuesto = etPresupuesto.text.toString()
        val solicitante = etSolicitante.text.toString()
        val descripcion = etDescripcion.text.toString()
        val estadoSeleccionado = spEstado.selectedItem.toString()
        val regionSeleccionada = spRegion.selectedItem.toString()
        val fechaInicio = spFechai.text.toString()
        val fechaTermino = spFechat.text.toString()

        // validacion de los campos
        if (codigo.isBlank() || nombre.isBlank() || ubicacion.isBlank() || presupuesto.isBlank() || solicitante.isBlank()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagenUri == null) {
            Toast.makeText(this, "Selecciona una imagen para el proyecto", Toast.LENGTH_SHORT).show()
            return
        }

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("proyectos/$codigo/${imagenUri!!.lastPathSegment}")

        storageRef.putFile(imagenUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { url ->
                val db = FirebaseFirestore.getInstance()
                val nuevoProyecto = hashMapOf(
                    "codigo" to codigo,
                    "nombre" to nombre,
                    "ubicacion" to ubicacion,
                    "presupuesto" to presupuesto,
                    "solicitante" to solicitante,
                    "descripcion" to descripcion,
                    "estado" to estadoSeleccionado,
                    "region" to regionSeleccionada,
                    "fecha_i" to fechaInicio,
                    "fecha_t" to fechaTermino,
                    "docUrl" to url.toString()
                )

                db.collection("Proyectos").document(codigo)
                    .set(nuevoProyecto)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Proyecto guardado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar proyecto: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
