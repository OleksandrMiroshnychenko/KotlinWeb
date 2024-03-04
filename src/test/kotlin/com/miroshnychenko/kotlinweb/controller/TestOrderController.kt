package com.miroshnychenko.kotlinweb.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.OrderStatus
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.service.OrderService
import com.miroshnychenko.kotlinweb.session.SessionObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(OrderController::class)
@DisplayName("Checks if order controller works as supposed and ")
class TestOrderController {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val mapper: ObjectMapper? = null

    @MockBean
    private val service: OrderService? = null

    @Test
    @DisplayName("creates order for the session user and sets user to it.")
    @Throws(Exception::class)
    fun checksOrderCreation() {
        val order = Order()
        order.id = 1L
        val user = User()
        user.email = "test@test.com"
        user.password = "test123"
        val content = mapper!!.writeValueAsString(order)
        val session = MockHttpSession()
        session.setAttribute(SessionObject.SESSION_USER.name, user)
        mockMvc!!.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(user.toString(), Objects.requireNonNull(mapper.readValue(result.response.contentAsString, Order::class.java).user).toString()) }
                .andExpect { assertEquals(1L, session.getAttribute(SessionObject.SESSION_ORDER.name)) }
    }

    @Test
    @DisplayName("returns the checked out order getting from the session id.")
    @Throws(Exception::class)
    fun checksOrderCheckout() {
        val id = 1L
        val resultOrder = Order()
        resultOrder.id = id
        resultOrder.status = OrderStatus.DONE
        val session = MockHttpSession()
        Mockito.`when`(service?.checkout(id)).thenReturn(resultOrder)
        session.setAttribute(SessionObject.SESSION_ORDER.name, id)
        mockMvc!!.perform(MockMvcRequestBuilders.get("/order/checkout")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(resultOrder.toString(), mapper?.readValue(result.response.contentAsString, Order::class.java).toString()) }
    }

    @Test
    @DisplayName("returns the order getting from the session id.")
    @Throws(Exception::class)
    fun returnsOrder() {
        val id = 1L
        val order = Order()
        order.id = id
        val session = MockHttpSession()
        Mockito.`when`(service?.getOrder(id)).thenReturn(order)
        session.setAttribute(SessionObject.SESSION_ORDER.name, id)
        mockMvc!!.perform(MockMvcRequestBuilders.get("/order")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(order.toString(), mapper?.readValue(result.response.contentAsString, Order::class.java).toString()) }
    }
}