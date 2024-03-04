package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@DisplayName("Check if order repository works as supposed with order interactions and ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestOrderRepository {
    private val email = "test@test.com"

    private var user: User? = null
    private var order: Order? = null

    @Autowired
    private val repository: OrderRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val productRepository: ProductRepository? = null

    @Autowired
    private val itemRepository: ProductItemRepository? = null

    @BeforeAll
    fun setUp() {
        user = User(email, "pass_123")
        order = Order()
        userRepository!!.save(user!!)
        order?.user = user
        repository?.save(order!!)
        val items: MutableList<ProductItem?> = ArrayList()
        for (i in 1..5) {
            val item = ProductItem()
            item.order = order
            val product = Product()
            product.title = "Product_$i"
            product.price = i * 2.5
            product.available = i * 5
            productRepository!!.save(product)
            item.product = product
            item.quantity = i * 2
            item.price = product.price * item.quantity
            items.add(item)
            itemRepository!!.save(item)
        }
        order?.items = items
        repository?.save(order!!)
    }

    @Test
    @DisplayName("finds order by the user.")
    fun checkTheOrderIsFoundByUser() {
        assertEquals(repository!!.findOrderByUser(userRepository!!.findUserByEmail(email)), order)
    }

    @Test
    @DisplayName("calculates the order price correctly.")
    fun calculatesTheOrderPriceInCheckoutTime() {
        val id: Long? = order?.id
        repository!!.calculatePrice(id)
        assertNotNull(id)
        assertEquals(275.0, repository.findByIdOrNull(id!!)?.price)
    }

    @Test
    @DisplayName("remove the user‘s orders which were canceled.")
    fun removesUserOrdersByStatus() {
        val canceled = Order()
        canceled.status = OrderStatus.CANCELED
        canceled.user = user
        repository!!.save(canceled)
        repository.deleteOrdersByUserAndStatus(user, OrderStatus.CANCELED)
        assertTrue(repository.findAll().filter { o -> o?.user == user }.map { it?.status }.none(OrderStatus.CANCELED::equals))
    }
}