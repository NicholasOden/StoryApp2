package com.example.picodiploma.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class UserRegistration(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
