package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.Result

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String): LiveData<Result<Any>> {
        return userRepository.register(name, email, password)
    }
}
