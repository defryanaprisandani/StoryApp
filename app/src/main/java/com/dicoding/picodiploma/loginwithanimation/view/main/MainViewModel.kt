package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    val storyList: LiveData<PagingData<ListStoryItem>> = repository.getStories()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
