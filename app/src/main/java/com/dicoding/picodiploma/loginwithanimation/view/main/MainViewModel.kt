package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.ListStoryItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyList = MutableLiveData<PagingData<ListStoryItem>>()
    val storyList: LiveData<PagingData<ListStoryItem>> = _storyList

    init {
        fetchStories()
    }

    private fun fetchStories() {
        viewModelScope.launch {
            repository.getStories()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _storyList.value = pagingData
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
