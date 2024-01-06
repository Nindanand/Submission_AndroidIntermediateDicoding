package com.example.aplikasistory.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplikasistory.data.model.UserRepository
import com.example.aplikasistory.data.model.response.ErrorResponse
import com.example.aplikasistory.data.model.response.ListStoryItem
import com.example.aplikasistory.data.model.response.StoryResponse
import com.example.aplikasistory.data.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val repository: UserRepository) : ViewModel()  {

    private val _listStoryUser = MutableLiveData<List<ListStoryItem>>()
    val listStoryUser: LiveData<List<ListStoryItem>> = _listStoryUser

    private val _error = MutableLiveData<ErrorResponse>()
    val error: MutableLiveData<ErrorResponse> = _error

    fun getStory(token: String) {
        val apiService = ApiConfig.getApiService(token)
        val client = apiService.getStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val updatedList = mutableListOf<ListStoryItem>()
                        it.listStory.let { stories ->
                            updatedList.addAll(stories)
                        }
                        _listStoryUser.postValue(updatedList)
                    }
                } else {
                    response.errorBody()?.let {
                        val jsonInString = response.errorBody()?.string()
                        Log.d("StoryViewModel", "Error response: $jsonInString")
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        val errorMessage = errorBody?.message ?: "Unknown error occurred"
                        error.postValue(ErrorResponse(message = errorMessage))
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.d("StoryViewModel", "OnFailure : ${t.message}")
                error.postValue(ErrorResponse(message = t.message))
            }
        })
    }

}