package com.tsehsrah.maxdrx.di

import com.tsehsrah.maxdrx.views.EditorFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    fun provideEditorFragmentFactory(): EditorFragmentFactory {
        return EditorFragmentFactory()
    }
}