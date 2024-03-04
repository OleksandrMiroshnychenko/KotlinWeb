package com.miroshnychenko.kotlinweb.listener

import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.service.OrderService
import com.miroshnychenko.kotlinweb.session.SessionObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.core.session.SessionDestroyedEvent
import org.springframework.stereotype.Component
import javax.servlet.http.HttpSession

@Component
class SessionExpiredListener @Autowired constructor(private val service: OrderService) : ApplicationListener<SessionDestroyedEvent> {

    override fun onApplicationEvent(event: SessionDestroyedEvent) {
        val user: User = (event.source as HttpSession).getAttribute(SessionObject.SESSION_USER.name) as User
        service.removeOrders(user)
    }
}
