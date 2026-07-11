package com.iti.pocketshop.features.aichat.di

import android.content.Context
import com.iti.pocketshop.features.aichat.data.repository.OllamaAiRepository
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import com.iti.pocketshop.features.aichat.util.SpeechToTextRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiChatModule {

    @Provides
    @Singleton
    fun provideAiRepository(
        ollamaAiRepository: OllamaAiRepository
    ): AiRepository = ollamaAiRepository

    @Provides
    @Singleton
    fun provideSpeechRecognizer(
        @ApplicationContext context: Context
    ): SpeechToTextRecognizer = SpeechToTextRecognizer(context)
}
