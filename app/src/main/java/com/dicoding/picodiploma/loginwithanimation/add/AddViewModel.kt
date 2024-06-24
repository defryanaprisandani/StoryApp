package com.dicoding.picodiploma.loginwithanimation.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.Result
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val userRepository: UserRepository) : ViewModel() {
    val responseLiveData = MutableLiveData<Boolean>()

    fun addStory(multipartBody: MultipartBody.Part, requestBody: RequestBody): LiveData<Result<StoryResponse>> {
        return userRepository.addStory(multipartBody, requestBody)
    }
}
