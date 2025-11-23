package com.example.dashboard2.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val institution: String = "",
    val photoUrl: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: Long = 0L,
    val updatedAt: Long? = null
) {

    constructor() : this(
        uid = "",
        name = "",
        email = "",
        institution = "",
        photoUrl = "",
        createdAt = 0L,
        updatedAt = null
    )
}