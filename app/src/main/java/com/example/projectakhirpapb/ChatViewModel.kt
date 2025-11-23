package com.example.projectakhirpapb

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(prompt = event.prompt, bitmap = event.bitmap)

                    _chatState.update { it.copy(prompt = "") }

                    _chatState.update { it.copy(isLoading = true) }

                    viewModelScope.launch {
                        try {
                            if (event.bitmap != null) {
                                getResponseWithImage(event.prompt, event.bitmap)
                            } else {
                                getResponse(event.prompt)
                            }
                        } finally {
                            _chatState.update { it.copy(isLoading = false) }
                        }
                    }
                }
            }

            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update { it.copy(prompt = event.newPrompt) }
            }
        }
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                }
            )
        }
    }

    private suspend fun getResponse(prompt: String) {
        val chat: Chat = GeminiChatRepository.getResponse(prompt)
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, chat)
                }
            )
        }
    }

    private suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap) {
        val chat: Chat = GeminiChatRepository.getResponseWithImage(prompt, bitmap)
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, chat)
                }
            )
        }
    }
}