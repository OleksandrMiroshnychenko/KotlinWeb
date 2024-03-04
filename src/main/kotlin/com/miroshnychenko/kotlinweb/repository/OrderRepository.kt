package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.OrderStatus
import com.miroshnychenko.kotlinweb.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order?, Long?> {
    fun findOrderByUser(user: User?): Order?

    @Modifying
    @Query("update Order o set o.price=(SELECT sum(i.price) FROM ProductItem i) where o.id = :id")
    fun calculatePrice(@Param("id") id: Long?)

    fun deleteOrdersByUserAndStatus(user: User?, status: OrderStatus?)
}
