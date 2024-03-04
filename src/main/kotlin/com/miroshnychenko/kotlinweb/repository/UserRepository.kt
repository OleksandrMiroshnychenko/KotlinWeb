package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User?, Long?> {
    fun findUserByEmail(email: String?): User?
}
