package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun addProduct(product: Product): Product {
        return productRepository.save(product)
    }

    val all: List<Product?>
        get() = productRepository.findAll()

    fun removeItems(product: Product, productQuantity: Int) {
        product.available -= productQuantity
        productRepository.save(product)
    }
}