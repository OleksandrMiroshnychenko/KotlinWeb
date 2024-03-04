package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@DisplayName("Check if user repository works as supposed with user interactions and ")
class TestUserRepository {
    @Autowired
    private val repository: UserRepository? = null

    @Test
    @DisplayName("finds user by the set email.")
    fun checkUserIsInRepoWithCorrectEmail() {
        val user = User("some_mail@gmail.com", "pass_123")
        repository!!.save(user)
        val userByEmail: User? = repository.findUserByEmail(user.email)
        Assertions.assertNotNull(userByEmail)
    }
}