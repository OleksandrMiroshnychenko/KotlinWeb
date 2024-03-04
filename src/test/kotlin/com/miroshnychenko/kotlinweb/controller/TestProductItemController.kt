package com.miroshnychenko.kotlinweb.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.service.OrderService
import com.miroshnychenko.kotlinweb.service.ProductItemService
import com.miroshnychenko.kotlinweb.session.SessionObject
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(ProductItemController::class)
@DisplayName("Checks if product item controller works as supposed and ")
class TestProductItemController {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val mapper: ObjectMapper? = null

    @MockkBean
    private val service: ProductItemService? = null

    @MockkBean
    private val orderService: OrderService? = null

    @Test
    @DisplayName("creates product item.")
    @Throws(Exception::class)
    fun checksItemCreation() {
        val item = ProductItem()
        item.quantity = 20
        val orderItem = ProductItem()
        orderItem.price = 100.0
        val content = mapper!!.writeValueAsString(item)
        val session = MockHttpSession()
        session.setAttribute(SessionObject.SESSION_ORDER.name, 1L)
        every { service?.addItem(any(), any()) } returns orderItem
        every { orderService?.getOrder(any()) } returns null
        mockMvc?.perform(MockMvcRequestBuilders.post("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(content))
                ?.andExpect(MockMvcResultMatchers.status().isOk())
                ?.andExpect { result: MvcResult -> assertEquals(orderItem.price, mapper.readValue(result.response.contentAsString, ProductItem::class.java).price) }
    }

    @Test
    @DisplayName("removes item and returns what’s been removed.")
    @Throws(Exception::class)
    fun checksItemRemoving() {
        val item = ProductItem()
        val id = 1L
        item.id = id
        item.quantity = 20
        val content = mapper!!.writeValueAsString(item)
        val session = MockHttpSession()
        session.setAttribute(SessionObject.SESSION_ORDER.name, id)
        every { service?.removeItem(any()) } returns item
        every { orderService?.removeItem(any(), any()) } returns null
        mockMvc!!.perform(MockMvcRequestBuilders.delete("/item/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(item.toString(), mapper.readValue(result.response.contentAsString, ProductItem::class.java).toString()) }
    }

    @Test
    @DisplayName("updates product item.")
    @Throws(Exception::class)
    fun checksOrderUpdating() {
        val item = ProductItem()
        item.quantity = 20
        val updatedItem = ProductItem()
        updatedItem.quantity = 100
        val content = mapper!!.writeValueAsString(item)
        every { service?.updateItem(any(), any()) } returns updatedItem
        mockMvc!!.perform(MockMvcRequestBuilders.put("/item/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> assertEquals(updatedItem.toString(), mapper.readValue(result.response.contentAsString, ProductItem::class.java).toString()) }
    }
}