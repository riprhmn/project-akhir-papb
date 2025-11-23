package com.example.dashboard2

import android.graphics.Bitmap

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null,
    val isLoading: Boolean = false
)