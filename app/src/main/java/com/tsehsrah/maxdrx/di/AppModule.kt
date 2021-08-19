package com.tsehsrah.maxdrx.di

import com.tsehsrah.maxdrx.repos.IImageRepository
import com.tsehsrah.maxdrx.repos.ImageRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private fun getServiceLocator():IServiceLocator{
        return ServiceLocator
    }
    @Provides
    fun provideServiceLocator():IServiceLocator{
        return getServiceLocator()
    }

    @Singleton
    @Provides
    fun provideImageRepository():IImageRepository{
        return ImageRepo(getServiceLocator())
    }

}