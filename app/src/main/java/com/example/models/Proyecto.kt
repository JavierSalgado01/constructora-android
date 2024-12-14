package com.example.models

data class Proyecto(
    var codigo: String = "",
    val nombre: String = "",
    val ubicacion: String = "",
    val estado: String = "",
    val fecha_i: String = "",
    val fecha_t: String = "",
    val presupuesto: String = "",
    val region: String = "",
    val solicitante: String = "",
    val descripcion: String = "",
    val docUrl: String = ""
)
