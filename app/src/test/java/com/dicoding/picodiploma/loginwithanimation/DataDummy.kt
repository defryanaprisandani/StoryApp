package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.AddResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.LoginResult
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.StoryResponse
import java.io.File

object DataDummy {
    fun generateToken() = "dummy_token_12345"
    fun generateName() = "dummy_user"
    fun generateEmail() = "dummy.email@example.com"
    fun generatePassword() = "dummy_password"

    fun generateUserModel() = UserModel(
        email = generateEmail(),
        token = generateToken(),
        isLogin = true
    )

    fun generateFile() = File("dummy_file")
    fun generateDesc() = "dummy_description"

    fun generateErrorResponse() = "dummy_error"

    fun generateLoginResponse() = LoginResponse(
        LoginResult(
            generateName(),
            "dummy_id",
            generateToken()
        ), false, "dummy_success"
    )

    fun generateRegisterResponse() = RegisterResponse(false, "dummy_success")

    fun generateStoryList() = mutableListOf<ListStoryItem>().apply {
        for (i in 1..5) {
            add(
                ListStoryItem(
                    id = "dummy_id_$i",
                    photoUrl = "dummy_photo_url_$i",
                    createdAt = "dummy_created_at_$i",
                    name = generateName(),
                    description = "dummy_description_$i",
                    lon = i.toDouble(),
                    lat = i.toDouble()
                )
            )
        }
    }

    fun generateStoryResponse() = StoryResponse(generateStoryList(), false, "dummy_success")

    fun generateAddResponse() = AddResponse(
        error = false,
        message = "dummy_success"
    )
}
