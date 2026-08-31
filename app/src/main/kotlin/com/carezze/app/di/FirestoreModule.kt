package com.fpculcasi.carezze.di

import com.fpculcasi.carezze.data.repository.MedicationLogRepositoryImpl
import com.fpculcasi.carezze.data.repository.PersonRepositoryImpl
import com.fpculcasi.carezze.data.repository.TherapyRepositoryImpl
import com.fpculcasi.carezze.data.repository.UserRepositoryImpl
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import com.fpculcasi.carezze.domain.repository.PersonRepository
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import com.fpculcasi.carezze.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirestoreModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPersonRepository(impl: PersonRepositoryImpl): PersonRepository

    @Binds
    @Singleton
    abstract fun bindTherapyRepository(impl: TherapyRepositoryImpl): TherapyRepository

    @Binds
    @Singleton
    abstract fun bindMedicationLogRepository(impl: MedicationLogRepositoryImpl): MedicationLogRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
