package com.plantify.plantify_app.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "", // "comprador" o "vendedor"
    val fotoPerfil: String = ""
)
