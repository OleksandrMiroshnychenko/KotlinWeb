package com.miroshnychenko.kotlinweb.filter.wrapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.miroshnychenko.kotlinweb.entity.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.StreamUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class UserRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val encodedPassword: String
    private val passParam = "password"

    private var streamCopy: String


    init {
        var password = request.getParameter(passParam)
        var user: User? = null
        streamCopy = StreamUtils.copyToString(request.inputStream, StandardCharsets.UTF_8)
        if (password == null && streamCopy != "") {
            user = ObjectMapper().readValue(streamCopy, User::class.java)
            if (user?.password != null) {
                password = user.password
            }
        }
        if (password != null) {
            password = BCryptPasswordEncoder().encode(password)
            if (user != null) {
                user.password = password
                streamCopy = ObjectMapper().writeValueAsString(user)
            }
        }
        this.encodedPassword = password
    }

    override fun getParameterValues(name: String): Array<String> {
        if (name == passParam) {
            return arrayOf(encodedPassword)
        }
        return super.getParameterValues(name)
    }

    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(streamCopy)
    }

    override fun getReader(): BufferedReader {
        val byteArrayInputStream = ByteArrayInputStream(streamCopy.toByteArray())
        return BufferedReader(InputStreamReader(byteArrayInputStream))
    }
}