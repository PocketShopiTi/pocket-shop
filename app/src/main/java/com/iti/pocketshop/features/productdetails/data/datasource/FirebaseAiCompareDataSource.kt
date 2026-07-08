package com.iti.pocketshop.features.productdetails.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseAiCompareDataSource @Inject constructor() : AiCompareDataSource {


    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(MODEL_NAME)
    }

    override suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val response = generativeModel.generateContent(prompt)
        response.text ?: throw IllegalStateException("Gemini returned an empty response")
    }

    private companion object {
        const val MODEL_NAME = "gemini-2.5-flash"
    }
}