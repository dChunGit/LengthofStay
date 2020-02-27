package com.simplex.whatsup.viewmodels

import androidx.lifecycle.ViewModel
import com.simplex.whatsup.api.mongo.MongoRepository
import com.simplex.whatsup.models.User
import io.reactivex.Single
import org.koin.core.KoinComponent

class LogInViewModel(private val mongoRepository: MongoRepository): ViewModel(), KoinComponent {
    fun addUser(email: String, username: String): Single<Any> {
        val user = User(
            id = "",
            email = email,
            name = username,
            karma = 0,
            subscribed_events = emptyList(),
            reports = emptyList()
        )
        return mongoRepository.addUser(user)
    }
}