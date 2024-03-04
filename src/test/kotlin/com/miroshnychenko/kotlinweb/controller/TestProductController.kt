package com.miroshnychenko.kotlinweb.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.service.ProductService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(ProductController::class)
@DisplayName("Checks if product controller works as supposed and ")
class TestProductController {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val mapper: ObjectMapper? = null

    @MockBean
    private val service: ProductService? = null

    @Test
    @DisplayName("creates product.")
    @Throws(Exception::class)
    fun checksOrderCreation() {
        val product = Product()
        product.title = "Product"
        product.price = 20.0
        val content = mapper!!.writeValueAsString(product)
        Mockito.`when`(service?.addProduct(ArgumentMatchers.any())).thenReturn(product)
        mockMvc!!.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> Assertions.assertEquals(content, result.response.contentAsString) }
    }

    @Test
    @DisplayName("returns the the all product list.")
    @Throws(Exception::class)
    fun checksOrderCheckout() {
        val products: Array<Product?> = arrayOfNulls(5)
        for (i in 1..5) {
            products[i - 1] = Product()
            products[i - 1]?.title = "Product_$i"
            products[i - 1]?.price = (i * 10).toDouble()
            products[i - 1]?.available = i * 5
        }
        Mockito.`when`(service?.all).thenReturn(products.asList())
        mockMvc!!.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect { result: MvcResult -> Assertions.assertEquals(products.contentToString(), mapper!!.readValue(result.response.contentAsString, Array<Product>::class.java).contentToString()) }
    }
}