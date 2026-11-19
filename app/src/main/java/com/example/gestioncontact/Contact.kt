package com.example.gestioncontact

import java.io.Serializable

data class Contact(
    var id: Long = 0L,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String? = null,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable
