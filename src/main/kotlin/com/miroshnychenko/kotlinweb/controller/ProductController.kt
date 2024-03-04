package com.miroshnychenko.kotlinweb.controller

import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(private val service: ProductService) {

    @get:GetMapping("products")
    val allProducts: List<Product?>
        get() = service.all

    @PostMapping("product")
    fun addProduct(@RequestBody product: Product): Product {
        return service.addProduct(product)
    }
}