package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.OrderStatus
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.repository.OrderRepository
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
import java.util.*

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if order service works as supposed and ")
@SpringBootTest(classes = [OrderService::class])
class TestOrderService {
    @Autowired
    private val service: OrderService? = null

    @MockkBean
    private val itemService: ProductItemService? = null

    @MockkBean
    private val repository: OrderRepository? = null

    @Test
    @DisplayName("returns the order after creation with id.")
    fun checksAddingOrder() {
        val order = Order()
        val repoOrder = Order()
        repoOrder.id = 1L
        every { repository?.save(order) } returns repoOrder
        val serviceOrder: Order = service!!.addOrder(order)
        assertEquals(repoOrder, serviceOrder)
    }

    @Test
    @DisplayName("returns the order by its id after calculating its price.")
    fun checksGettingAndCalculationOrderPriceById() {
        val order = Order()
        val id = 1L
        order.id = id
        every { repository?.findByIdOrNull(id) } returns order
        every { repository?.calculatePrice(id) } returns null
        val serviceOrder: Order? = service!!.getOrder(id)
        verify(exactly = 1) {repository?.calculatePrice(id)}
        assertEquals(order, serviceOrder)
    }

    @Test
    @DisplayName("removes the created orders by the user.")
    fun checksRemovingSpecificOrders() {
        val user = User()
        every { repository?.deleteOrdersByUserAndStatus(user, OrderStatus.CREATED) } returns null
        service!!.removeOrders(user)
        verify(exactly = 1) {repository?.deleteOrdersByUserAndStatus(user, OrderStatus.CREATED)}
    }

    @Test
    @DisplayName("sets the correct status for the order on the checkout.")
    fun checksOrderCheckingOut() {
        val id = 1L
        val repoOrder = Order()
        repoOrder.status = OrderStatus.DONE
        val items: MutableList<ProductItem?> = ArrayList()
        for (i in 1..5) {
            items.add(ProductItem())
        }
        repoOrder.items = items
        every { repository?.findByIdOrNull(id) } returns repoOrder
        every { repository?.calculatePrice(id) } returns null
        every { repository?.save(repoOrder) } returns null
        every { itemService?.checkoutItem(any()) } returns null
        val serviceOrder: Order? = service!!.checkout(id)
        verify { itemService?.checkoutItem(any()) }
        verify { repository?.calculatePrice(id) }
        assertEquals(OrderStatus.DONE, serviceOrder?.status)
    }

    @Test
    @DisplayName("should remove item from the order and save it.")
    fun checksRemovingItemFromOrder() {
        val id = 1L
        val repoOrder = Order()
        repoOrder.status = OrderStatus.DONE
        val items: MutableList<ProductItem> = ArrayList<ProductItem>()
        for (i in 1..5) {
            items.add(ProductItem())
        }
        repoOrder.items = ArrayList(items)
        every { repository?.findByIdOrNull(id) } returns repoOrder
        every { repository?.calculatePrice(id) } returns null
        every { repository?.save(repoOrder) } returns null
        service!!.removeItem(id, items[0])
        verify(exactly = 1) {repository?.save(repoOrder)}
        val repoItems: List<ProductItem?>? = repoOrder.items
        Assertions.assertNotNull(repoItems)
        assertEquals(4, repoItems?.size)
    }
}