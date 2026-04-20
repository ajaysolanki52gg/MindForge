package com.mindforge.app.di

import android.content.Context
import androidx.room.Room
import com.mindforge.app.data.local.MindForgeDao
import com.mindforge.app.data.local.MindForgeDatabase
import com.mindforge.app.data.repository.OfflineMindForgeRepository
import com.mindforge.app.domain.repository.MindForgeRepository
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
    fun provideDatabase(@ApplicationContext context: Context): MindForgeDatabase =
        Room.databaseBuilder(context, MindForgeDatabase::class.java, "mindforge.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(database: MindForgeDatabase): MindForgeDao = database.mindForgeDao()

    @Provides
    @Singleton
    fun provideRepository(dao: MindForgeDao): MindForgeRepository = OfflineMindForgeRepository(dao)
}
