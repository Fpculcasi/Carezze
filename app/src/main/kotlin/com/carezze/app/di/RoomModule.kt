package com.fpculcasi.carezze.di

import android.content.Context
import androidx.room.Room
import com.fpculcasi.carezze.data.local.db.CarezzeDatabase
import com.fpculcasi.carezze.data.local.db.dao.ActivityLogDao
import com.fpculcasi.carezze.data.local.db.dao.MedicationLogDao
import com.fpculcasi.carezze.data.local.db.dao.TherapyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): CarezzeDatabase =
        Room
            .databaseBuilder(context, CarezzeDatabase::class.java, "carezze.db")
            .build()

    @Provides
    fun provideActivityLogDao(db: CarezzeDatabase): ActivityLogDao = db.activityLogDao()

    @Provides
    fun provideTherapyDao(db: CarezzeDatabase): TherapyDao = db.therapyDao()

    @Provides
    fun provideMedicationLogDao(db: CarezzeDatabase): MedicationLogDao = db.medicationLogDao()
}
