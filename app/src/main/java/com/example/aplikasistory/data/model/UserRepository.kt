package com.example.aplikasistory.data.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.aplikasistory.data.model.response.StoryUploadResponse
import com.example.aplikasistory.data.pref.UserModel
import com.example.aplikasistory.data.pref.UserPreference
import com.example.aplikasistory.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference
) {
    private val _uploadStory  = MutableLiveData<StoryUploadResponse>()

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) {
        val client = ApiConfig.getApiService(token).uploadStory(file, description)
        client.enqueue(object : Callback<StoryUploadResponse> {
            override fun onResponse(
                call: Call<StoryUploadResponse>,
                response: Response<StoryUploadResponse>
            ) {
                if(response.isSuccessful){
                    _uploadStory.value = response.body()
                } else {
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                Log.d("onFailure: ", t.message.toString())
            }

        })

    }
    companion object {
        const val TAG = "UserRepository"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }

}