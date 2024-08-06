package com.example.citiesearch.hilt


import com.example.citiesearch.model.CitiesLocalDataSource
import com.example.citiesearch.model.CitiesRepository
import com.example.citiesearch.model.CitiesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideCityRepository(
        localDataSource: CitiesLocalDataSource
    ): CitiesRepository {
        return CitiesRepositoryImpl(localDataSource)
    }
}
