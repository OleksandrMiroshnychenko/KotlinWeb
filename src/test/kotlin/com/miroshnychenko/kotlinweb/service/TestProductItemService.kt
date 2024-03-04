package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.repository.ProductItemRepository
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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if user service works as supposed and ")
@SpringBootTest(classes = [ProductItemService::class])
class TestProductItemService {
    @Autowired
    private val service: ProductItemService? = null

    @MockkBean
    private val productService: ProductService? = null

    @MockkBean
    private val repository: ProductItemRepository? = null

    @Test
    @DisplayName("returns the item after success creation with id.")
    fun checksAdding() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 20
        val order = Order()
        val repoItem = ProductItem()
        repoItem.id = 1L
        repoItem.quantity = 4
        repoItem.product = product
        repoItem.order = order
        repoItem.price = 60.0
        every { repository?.save(repoItem) } returns repoItem
        val serviceItem: ProductItem? = service!!.addItem(order, repoItem)
        Assertions.assertNotNull(serviceItem?.id)
        assertEquals(repoItem, serviceItem)
        val items: MutableList<ProductItem?>? = order.items
        assertEquals(order, repoItem.order)
        Assertions.assertNotNull(items)
        assertEquals(1, items?.size)
        assertEquals(repoItem, items!![0])
    }

    @Test
    @DisplayName("returns null if the product quantity is not enough.")
    fun checkReturningNullIfProductIsNotEnough() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 1
        val order = Order()
        val repoItem = ProductItem()
        repoItem.id = 1L
        repoItem.quantity = 4
        repoItem.product = product
        repoItem.order = order
        every { repository?.save(repoItem) } returns repoItem
        Assertions.assertNull(service!!.addItem(order, repoItem))
    }

    @Test
    @DisplayName("returns calculates item price correctly.")
    fun checkItemPriceCalculating() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 1
        val repoItem = ProductItem()
        repoItem.quantity = 4
        repoItem.product = product
        service!!.calculateItemPrice(repoItem)
        assertEquals(60.0, repoItem.price)
    }

    @Test
    @DisplayName("returns item after removing.")
    fun checkItemRemoving() {
        val id = 1L
        every { repository?.removeById(id) } returns null
        service!!.removeItem(id)
        verify(exactly = 1) { repository?.removeById(id) }
    }

    @Test
    @DisplayName("returns successfully updated item price.")
    fun checkItemUpdatedCorrectly() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 10
        val item = ProductItem()
        val repoItem = ProductItem()
        val id = 1L
        repoItem.id = id
        repoItem.quantity = 4
        repoItem.product = product
        repoItem.price = 60.0
        every { repository?.findByIdOrNull(any()) } returns item
        every { repository?.save(any()) } returns repoItem
        val serviceItem: ProductItem? = service!!.updateItem(id, repoItem)
        Assertions.assertNotNull(serviceItem)
        assertEquals(60.0, serviceItem?.price)
        assertEquals(id, serviceItem?.id)
    }

    @Test
    @DisplayName("returns null if unsuccessfully updated item price.")
    fun checkUpdatingMissingItem() {
        every { repository?.findByIdOrNull(any()) } returns null
        Assertions.assertNull(service!!.updateItem(1L, ProductItem()))
    }

    @Test
    @DisplayName("checkouts item successfully.")
    fun checkCheckoutsItem() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 10
        val item = ProductItem()
        item.quantity = 4
        item.product = product
        every { productService?.removeItems(any(), any()) } returns null
        service!!.checkoutItem(item)
        verify(exactly = 1) { productService?.removeItems(product, item.quantity) }
    }

    @Test
    @DisplayName("makes no checkout if there are not enough items.")
    fun checkNotCheckoutItem() {
        val product = Product()
        product.title = "Test"
        product.price = 15.0
        product.available = 1
        val item = ProductItem()
        item.quantity = 4
        item.product = product
        service!!.checkoutItem(item)
        verify(exactly = 0) { productService?.removeItems(product, item.quantity) }
    }
}