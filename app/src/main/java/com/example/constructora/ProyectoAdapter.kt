package com.example.constructora

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.models.Proyecto

class ProyectoAdapter(
    private val proyectos: List<Proyecto>,
    private val onDeleteClick: (Proyecto) -> Unit
) : RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder>() {

    class ProyectoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenProyecto: ImageView = itemView.findViewById(R.id.ivImagenProyecto) // Vincula el ImageView
        val nombreProyecto: TextView = itemView.findViewById(R.id.tvNombreProyecto)
        val ubicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val codigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnVerMas: Button = itemView.findViewById(R.id.btnVerMas)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = proyectos[position]

        holder.nombreProyecto.text = proyecto.nombre
        holder.ubicacion.text = proyecto.ubicacion
        holder.codigo.text = proyecto.codigo

        Glide.with(holder.itemView.context)
            .load(proyecto.docUrl) // url publico de la imagen
            .placeholder(R.drawable.cargando) // img de carga inicial
            .error(R.drawable.imgerror) // img de error
            .into(holder.imagenProyecto) // imageview donde se carga

        // click de los botones
        holder.btnEliminar.setOnClickListener {
            // Mostrar un cuadro de diálogo de confirmación
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar eliminacion")
                .setMessage("¿Seguro de que deseas eliminar este proyecto?")
                .setPositiveButton("Eliminar") { dialog, _ ->
                    // Si el usuario confirma, llamar a la función onDeleteClick
                    onDeleteClick(proyecto)
                    dialog.dismiss() // Cerrar el diálogo
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    // Si el usuario cancela, cerrar el diálogo
                    dialog.dismiss()
                }
                .show()
        }


        holder.btnModificar.setOnClickListener {
            // intent que extrae la info
            val context = holder.itemView.context
            val intent = Intent(context, Editar::class.java)

            // Pasar datos del proyecto al Intent
            intent.putExtra("codigo", proyecto.codigo)
            intent.putExtra("nombre", proyecto.nombre)
            intent.putExtra("ubicacion", proyecto.ubicacion)
            intent.putExtra("descripcion", proyecto.descripcion)
            intent.putExtra("estado", proyecto.estado)
            intent.putExtra("fecha", proyecto.fecha_i)
            intent.putExtra("docUrl", proyecto.docUrl) // Imagen del proyecto

            // Iniciar la actividad Editar
            context.startActivity(intent)
        }

        holder.btnVerMas.setOnClickListener {
            val intent = Intent(holder.itemView.context, Detalles::class.java)

            intent.putExtra("codigo", proyecto.codigo)
            intent.putExtra("nombre", proyecto.nombre)
            intent.putExtra("descripcion", proyecto.descripcion)
            intent.putExtra("estado", proyecto.estado)
            intent.putExtra("fecha_i", proyecto.fecha_i)
            intent.putExtra("fecha_t", proyecto.fecha_t)
            intent.putExtra("presupuesto", proyecto.presupuesto)
            intent.putExtra("direccion", proyecto.ubicacion)
            intent.putExtra("region", proyecto.region)
            intent.putExtra("solicitante", proyecto.solicitante)
            intent.putExtra("docUrl", proyecto.docUrl) // Si es una URL o recurso

            holder.itemView.context.startActivity(intent)
        }


    }


    override fun getItemCount(): Int {
        return proyectos.size
    }
}
