package com.example.citiesearch.hilt

import android.content.Context
import com.example.citiesearch.model.CitiesLocalDataSource
import com.example.citiesearch.model.CitiesLocalDataSourceImpl

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        @ApplicationContext context: Context
    ): CitiesLocalDataSource {
        return CitiesLocalDataSourceImpl(context)
    }
}
