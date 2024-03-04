package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.OrderStatus
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.entity.User
import com.miroshnychenko.kotlinweb.repository.OrderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(private val orderRepository: OrderRepository, private val itemService: ProductItemService) {

    fun addOrder(order: Order): Order {
        return orderRepository.save(order)
    }

    fun getOrder(id: Long?): Order? {
        orderRepository.calculatePrice(id)
        return orderRepository.findByIdOrNull(id)
    }

    fun removeOrders(user: User?) {
        orderRepository.deleteOrdersByUserAndStatus(user, OrderStatus.CREATED)
    }

    fun checkout(id: Long?): Order? {
        val order: Order? = getOrder(id)
        for (item in order!!.items!!) {
            itemService.checkoutItem(item!!)
        }
        order.status = OrderStatus.DONE
        return order
    }

    //Check
    fun removeItem(id: Long?, removedItem: ProductItem?) {
        val order: Order? = getOrder(id)
        order?.items?.remove(removedItem)
        orderRepository.save(order!!)
    }
}