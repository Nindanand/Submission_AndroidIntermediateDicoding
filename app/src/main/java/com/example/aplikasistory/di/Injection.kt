package com.example.aplikasistory.di

import android.content.Context
import com.example.aplikasistory.data.model.UserRepository
import com.example.aplikasistory.data.pref.UserPreference
import com.example.aplikasistory.data.pref.dataStore
import com.example.aplikasistory.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}