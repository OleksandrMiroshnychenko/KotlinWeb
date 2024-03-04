package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun addUser(user: User): User {
        return userRepository.save(user)
    }

    fun getUserByEmail(email: String?): User? {
        return userRepository.findUserByEmail(email)
    }
}