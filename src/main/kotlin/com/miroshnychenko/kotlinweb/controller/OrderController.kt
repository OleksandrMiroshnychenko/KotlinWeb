package com.miroshnychenko.kotlinweb.controller

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.exception.DepotException
import com.miroshnychenko.kotlinweb.service.OrderService
import com.miroshnychenko.kotlinweb.session.SessionObject
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("order")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @Throws(DepotException::class)
    fun createOrder(@RequestBody order: Order, @Parameter(hidden = true) session: HttpSession): Order {
        val user: User = session.getAttribute(SessionObject.SESSION_USER.name) as User
        order.user = user
        orderService.addOrder(order)
        session.setAttribute(SessionObject.SESSION_ORDER.name, order.id)
        return order
    }

    @GetMapping("checkout")
    fun checkout(@Parameter(hidden = true) session: HttpSession): Order? {
        val id = session.getAttribute(SessionObject.SESSION_ORDER.name) as Long
        return orderService.checkout(id)
    }

    @GetMapping
    fun getOrder(@Parameter(hidden = true) session: HttpSession): Order? {
        val id = session.getAttribute(SessionObject.SESSION_ORDER.name) as Long
        return orderService.getOrder(id)
    }
}