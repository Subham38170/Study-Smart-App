package com.example.studysmart.di

import android.app.Application
import androidx.room.Room
import com.example.studysmart.data.local.AppDatabase
import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.data.local.SubjectDao
import com.example.studysmart.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = AppDatabase::class.java,
            name = "studysmart.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(appDatabase: AppDatabase): SubjectDao {
        return appDatabase.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(appDatabase: AppDatabase): SessionDao {
        return appDatabase.sessionDao()
    }


}