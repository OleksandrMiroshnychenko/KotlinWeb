package com.miroshnychenko.kotlinweb.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.exception.DepotException
import com.miroshnychenko.kotlinweb.service.UserService
import com.miroshnychenko.kotlinweb.session.SessionObject
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(UserController::class)
@DisplayName("Checks if user controller works as supposed and ")
class TestUserController {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val mapper: ObjectMapper? = null

    @MockkBean
    private val service: UserService? = null

    @MockkBean
    private val encoder: BCryptPasswordEncoder? = null

    @Test
    @DisplayName("returns success message when register the user.")
    @Throws(Exception::class)
    fun checksIfUserSuccessfullyRegister() {
        val user = User("test@test.com", "test123")
        val content = mapper!!.writeValueAsString(user)
        every { service?.getUserByEmail("test@test.com") } returns null
        every { service?.addUser(user) } returns null
        mockMvc!!.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @DisplayName("returns conflict message when register the same user twice.")
    @Throws(Exception::class)
    fun checksConflictInRegistration() {
        val user = User("test@test.com", "test123")
        val content = mapper!!.writeValueAsString(user)
        every { service?.getUserByEmail("test@test.com") } returns user
        mockMvc!!.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isConflict())
    }

    @Test
    @DisplayName("returns session id with the OK status if user successfully login.")
    @Throws(Exception::class)
    fun checkSuccessfulLogin() {
        val password = "test123"
        val user = User("test@test.com", password)
        val content = mapper!!.writeValueAsString(user)
        every { service?.getUserByEmail("test@test.com") } returns user
        every { encoder?.matches(password, password) } returns true
        val session = MockHttpSession()
        mockMvc!!.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(session.id, result.response.contentAsString) }
                .andExpect { assertEquals(user, session.getAttribute(SessionObject.SESSION_USER.name)) }
                .andExpect { assertEquals(15 * 60, session.maxInactiveInterval) }
    }

    @Test
    @DisplayName("throws exception with correct message and status.")
    @Throws(Exception::class)
    fun checkUnsuccessfulLogin() {
        val user = User("test@test.com", "test123")
        val content = mapper!!.writeValueAsString(user)
        every { service?.getUserByEmail("test@test.com") } returns null
        mockMvc!!.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect { result: MvcResult -> Assertions.assertTrue(result.resolvedException is DepotException) }
                .andExpect { result: MvcResult -> assertEquals("Can’t find the requested user: " + user.email, result.resolvedException!!.message) }
    }
}