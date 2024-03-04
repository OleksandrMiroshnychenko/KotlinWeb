package com.miroshnychenko.kotlinweb.filter

import com.miroshnychenko.kotlinweb.filter.wrapper.UserRequestWrapper
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.filter.FormContentFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserFilter : FormContentFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        filterChain.doFilter(UserRequestWrapper(request), response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.servletPath.startsWith("/registration")
    }

    @Bean
    fun filter(): FilterRegistrationBean<UserFilter> {
        val registrationBean = FilterRegistrationBean<UserFilter>()
        registrationBean.filter = UserFilter()
        registrationBean.addUrlPatterns("/registration/*")
        return registrationBean
    }
}