package com.example.constructora

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide // Para cargar imágenes desde URL

class Detalles : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        val tvCodigo = findViewById<TextView>(R.id.tvCodigo)
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvFechaInicio = findViewById<TextView>(R.id.tvFechaInicio)
        val tvFechaTermino = findViewById<TextView>(R.id.tvFechaTermino)
        val tvPresupuesto = findViewById<TextView>(R.id.tvPresupuesto)
        val tvUbicacion = findViewById<TextView>(R.id.tvUbicacion)
        val tvRegion = findViewById<TextView>(R.id.tvRegion)
        val tvSolicitante = findViewById<TextView>(R.id.tvSolicitante)
        val ivImagenProyecto = findViewById<ImageView>(R.id.ivImagenProyecto)

        val codigo = intent.getStringExtra("codigo")
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val estado = intent.getStringExtra("estado")
        val fechaInicio = intent.getStringExtra("fecha_i")
        val fechaTermino = intent.getStringExtra("fecha_t")
        val presupuesto = intent.getStringExtra("presupuesto")
        val direccion = intent.getStringExtra("direccion")
        val region = intent.getStringExtra("region")
        val solicitante = intent.getStringExtra("solicitante")
        val imagenUrl = intent.getStringExtra("docUrl")

        tvCodigo.text = "Código: $codigo"
        tvNombre.text = nombre
        tvDescripcion.text = descripcion
        tvEstado.text = "Estado: $estado"
        tvFechaInicio.text = "Fecha de inicio: $fechaInicio"
        tvFechaTermino.text = "Fecha de término: $fechaTermino"
        tvPresupuesto.text = "Presupuesto: $presupuesto"
        tvUbicacion.text = "Ubicación: $direccion"
        tvRegion.text = "Región: $region"
        tvSolicitante.text = "Solicitante: $solicitante"

        // cargar imagen con usando glide
        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.cargando)
                .error(R.drawable.imgerror)
                .into(ivImagenProyecto)
        } else {
            ivImagenProyecto.setImageResource(R.drawable.cargando)
        }
    }
}
