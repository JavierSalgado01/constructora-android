package com.example.constructora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Proyecto
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class MainActivity2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProyectoAdapter
    private val proyectos = mutableListOf<Proyecto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        //los reciler vistas
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProyectoAdapter(proyectos) { proyecto -> eliminarProyecto(proyecto) }
        recyclerView.adapter = adapter

        val btnAgregar: MaterialButton = findViewById(R.id.btnAgregar)
        val btnRefrescar: MaterialButton = findViewById(R.id.btnRefrescar)
        val btnSalir: MaterialButton = findViewById(R.id.btnSalir)

        //crar proyecto
        btnAgregar.setOnClickListener {
            val intent = Intent(this, Agregar::class.java)
            startActivity(intent)
        }

        //refrescar la pantalla
        btnRefrescar.setOnClickListener {
            refrescarPantalla()
        }

        // salir
        btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //finaliza actividad
        }
        cargarProyectos()
    }

    private fun refrescarPantalla() {
        // termina y reinicia la actividad actual
        finish()
        startActivity(intent)
    }

    private fun cargarProyectos() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Proyectos").get()
            .addOnSuccessListener { documents ->
                proyectos.clear()
                for (d in documents) {
                    val proyecto = d.toObject(Proyecto::class.java)
                    proyecto.codigo = d.id
                    proyectos.add(proyecto)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error detallado al cargar proyectos", exception)

                val errorMessage = when (exception) {
                    is FirebaseFirestoreException -> {
                        when (exception.code) {
                            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                                "no permisos"
                            FirebaseFirestoreException.Code.UNAVAILABLE ->
                                "conexion a internet rechazada"
                            else -> "error en firestore: ${exception.localizedMessage}"
                        }
                    }
                    else -> "error inesperado: ${exception.localizedMessage}"
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun eliminarProyecto(proyecto: Proyecto) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Proyectos").document(proyecto.codigo).delete()
            .addOnSuccessListener {
                proyectos.remove(proyecto)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Proyecto eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "error al eliminar proyecto", Toast.LENGTH_SHORT).show()
            }
    }
}

