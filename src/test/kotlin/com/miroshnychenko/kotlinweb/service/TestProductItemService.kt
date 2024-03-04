package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.repository.ProductItemRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Optional

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if user service works as supposed and ")
@SpringBootTest(classes = [ProductItemService::class])
class TestProductItemService {
    @Autowired
    private val service: ProductItemService? = null

    @MockBean
    private val productService: ProductService? = null

    @MockBean
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
        Mockito.`when`(repository?.save(repoItem)).thenReturn(repoItem)
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
        Mockito.`when`(repository?.save(repoItem)).thenReturn(repoItem)
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
        service!!.removeItem(id)
        Mockito.verify<ProductItemRepository>(repository, Mockito.times(1)).removeById(id)
    }

//    @Test
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
        Mockito.`when`(repository?.findByIdOrNull(ArgumentMatchers.any()!!)).thenReturn(item)
        Mockito.`when`(repository?.save(ArgumentMatchers.any())).thenReturn(repoItem)
        val serviceItem: ProductItem? = service!!.updateItem(id, repoItem)
        Assertions.assertNotNull(serviceItem)
        assertEquals(60, serviceItem?.price)
        assertEquals(id, serviceItem?.id)
    }

    @Test
    @DisplayName("returns null if unsuccessfully updated item price.")
    fun checkUpdatingMissingItem() {
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
        service!!.checkoutItem(item)
        Mockito.verify<ProductService>(productService, Mockito.times(1)).removeItems(product, item.quantity)
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
        Mockito.verify<ProductService>(productService, Mockito.times(0)).removeItems(product, item.quantity)
    }
}