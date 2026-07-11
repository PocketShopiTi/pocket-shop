package com.iti.pocketshop.features.aichat.util

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : TextToSpeech.OnInitListener {
    private val utteranceId = "utteranceId"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _currentSpeakParagraph = MutableStateFlow("")
    val currentSpeakParagraph = _currentSpeakParagraph.asStateFlow()

    private val chunkQueue: Queue<String> = LinkedList()
    private var onFinishedSuccessfully: () -> Unit = {}

    init {
        tts = TextToSpeech(context, this)
        tts?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.update { true }
                }

                override fun onDone(utteranceId: String?) {
                    speakNextChunk()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.update { false }
                }
            }
        )
    }

    fun speakNewChunks(newChunks: List<String>, onFinished: () -> Unit = {}) {
        chunkQueue.clear()
        chunkQueue.addAll(newChunks)
        onFinishedSuccessfully = onFinished
        speakNextChunk()
    }

    private fun speakNextChunk() {
        val nextChunk = chunkQueue.poll()
        if (nextChunk == null) {
            _isSpeaking.update { false }
            onFinishedSuccessfully()
            onFinishedSuccessfully = {}
        } else {
            _currentSpeakParagraph.update { nextChunk }
            speak(nextChunk)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isInitialized = true
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    fun stopSpeaking() {
        _isSpeaking.update { false }
        _currentSpeakParagraph.update { "" }
        chunkQueue.clear()
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
