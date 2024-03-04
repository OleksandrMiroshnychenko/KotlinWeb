package com.miroshnychenko.kotlinweb.controller

import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.exception.DepotException
import com.miroshnychenko.kotlinweb.service.UserService
import com.miroshnychenko.kotlinweb.session.SessionObject
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
class UserController(private val service: UserService, private val encoder: BCryptPasswordEncoder) {

    @PostMapping("registration")
    fun register(@RequestBody user: @Valid User?): ResponseEntity<*> {
        val userByEmail: User? = service.getUserByEmail(user!!.email)
        if (userByEmail != null) {
            return ResponseEntity("The current user already exists", HttpStatus.CONFLICT)
        }
        service.addUser(user)
        return ResponseEntity<Any>(HttpStatus.OK)
    }

    @PostMapping("login")
    @Throws(DepotException::class)
    fun login(@RequestBody user: @Valid User?, @Parameter(hidden = true) session: HttpSession): String {
        val userByEmail: User? = service.getUserByEmail(user!!.email)
        if (userByEmail != null && encoder.matches(user.password, userByEmail.password)) {
            session.setAttribute(SessionObject.SESSION_USER.name, userByEmail)
            session.maxInactiveInterval = 15 * 60
            return session.id
        }
        throw DepotException("Can’t find the requested user: " + user.email, "400")
    }
}