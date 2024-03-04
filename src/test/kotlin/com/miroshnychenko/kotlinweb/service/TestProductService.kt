package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.repository.ProductRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if user service works as supposed and ")
@SpringBootTest(classes = [ProductService::class])
class TestProductService {
    @Autowired
    private val service: ProductService? = null

    @MockkBean
    private val repository: ProductRepository? = null

    @Test
    @DisplayName("returns the product after creation with id.")
    fun checksAddingProduct() {
        val product = Product()
        val repoProduct = Product()
        repoProduct.id = 1L
        every { repository?.save(product) } returns repoProduct
        val serviceProduct: Product = service!!.addProduct(product)
        Assertions.assertNotNull(serviceProduct.id)
        assertEquals(repoProduct, serviceProduct)
    }

    @Test
    @DisplayName("returns all the products.")
    fun checksReturningAllProducts() {
        val items: MutableList<Product?> = ArrayList()
        for (i in 1..5) {
            items.add(Product())
        }
        every { repository?.findAll() } returns items
        val serviceItems: List<Product?> = service!!.all
        assertEquals(items, serviceItems)
    }

    @Test
    @DisplayName("removes specific quantity of products.")
    fun checksRemovingItemsFromProduct() {
        val product = Product()
        product.title = "Test"
        product.price = 10.0
        product.available = 30
        every { repository?.save(any()) } returns null
        service!!.removeItems(product, 10)
        verify(exactly = 1) { repository?.save(any()) }
        assertEquals(20, product.available)
    }
}