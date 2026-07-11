package com.iti.pocketshop.features.aichat.di

import com.iti.pocketshop.features.aichat.data.repository.OllamaAiRepository
import com.iti.pocketshop.features.aichat.domain.repository.AiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiChatModule {

    @Binds
    abstract fun bindAiRepository(
        ollamaAiRepository: OllamaAiRepository
    ): AiRepository
}
