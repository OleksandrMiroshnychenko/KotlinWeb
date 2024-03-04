package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.OrderStatus
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.repository.OrderRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockitogit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@DisplayName("Checks if order service works as supposed and ")
@SpringBootTest(classes = [OrderService::class])
class TestOrderService {
    @Autowired
    private val service: OrderService? = null

    @MockBean
    private val itemService: ProductItemService? = null

    @MockBean
    private val repository: OrderRepository? = null

    @Test
    @DisplayName("returns the order after creation with id.")
    fun checksAddingOrder() {
        val order = Order()
        val repoOrder = Order()
        repoOrder.id = 1L
        Mockito.`when`(repository?.save(order)).thenReturn(repoOrder)
        val serviceOrder: Order = service!!.addOrder(order)
        assertEquals(repoOrder, serviceOrder)
    }

    @Test
    @DisplayName("returns the order by its id after calculating its price.")
    fun checksGettingAndCalculationOrderPriceById() {
        val order = Order()
        val id = 1L
        order.id = id
        Mockito.`when`(repository?.findByIdOrNull(id)).thenReturn(order)
        val serviceOrder: Order? = service!!.getOrder(id)
        Mockito.verify<OrderRepository>(repository, Mockito.times(1)).calculatePrice(id)
        assertEquals(order, serviceOrder)
    }

    @Test
    @DisplayName("removes the created orders by the user.")
    fun checksRemovingSpecificOrders() {
        val user = User()
        service!!.removeOrders(user)
        Mockito.verify<OrderRepository>(repository, Mockito.times(1)).deleteOrdersByUserAndStatus(user, OrderStatus.CREATED)
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
        Mockito.`when`(repository?.findByIdOrNull(id)).thenReturn(repoOrder)
        val serviceOrder: Order? = service!!.checkout(id)
        Mockito.verify<ProductItemService>(itemService, Mockito.times(5)).checkoutItem(ArgumentMatchers.any())
        Mockito.verify<OrderRepository>(repository, Mockito.times(1)).calculatePrice(id)
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
        Mockito.`when`(repository?.findByIdOrNull(id)).thenReturn(repoOrder)
        service!!.removeItem(id, items[0])
        Mockito.verify<OrderRepository>(repository, Mockito.times(1)).save(repoOrder)
        val repoItems: List<ProductItem?>? = repoOrder.items
        Assertions.assertNotNull(repoItems)
        assertEquals(4, repoItems?.size)
    }
}