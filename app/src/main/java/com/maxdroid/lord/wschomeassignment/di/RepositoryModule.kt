package com.maxdroid.lord.wschomeassignment.di

import com.maxdroid.lord.wschomeassignment.data.repository.GamesRepositoryImpl
import com.maxdroid.lord.wschomeassignment.domain.repository.GamesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        gamesRepositoryImpl: GamesRepositoryImpl
    ): GamesRepository
}
