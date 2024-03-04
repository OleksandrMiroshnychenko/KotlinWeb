package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if user service works as supposed and ")
@SpringBootTest(classes = [UserService::class])
class TestUserService {
    @Autowired
    private val service: UserService? = null

    @MockBean
    private val repository: UserRepository? = null

    @Test
    @DisplayName("returns the user after creation with id.")
    fun checksAddingUser() {
        val user = User()
        val repoUser = User()
        repoUser.id = 1L
        Mockito.`when`(repository?.save(user)).thenReturn(repoUser)
        val serviceUser: User = service!!.addUser(user)
        Assertions.assertNotNull(serviceUser.id)
        assertEquals(repoUser, serviceUser)
    }

    @Test
    @DisplayName("returns the user by its email.")
    fun checksGettingUserByEmail() {
        val user = User()
        val email = "test@test.com"
        user.email = email
        user.password = email
        Mockito.`when`(repository?.findUserByEmail(user.email)).thenReturn(user)
        val serviceUser: User? = service!!.getUserByEmail(user.email)
        assertEquals(user, serviceUser)
    }
}