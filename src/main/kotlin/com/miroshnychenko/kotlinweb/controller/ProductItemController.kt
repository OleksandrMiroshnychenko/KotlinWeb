package com.miroshnychenko.kotlinweb.controller

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.service.OrderService
import com.miroshnychenko.kotlinweb.service.ProductItemService
import com.miroshnychenko.kotlinweb.session.SessionObject
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
@RequestMapping("/item")
class ProductItemController(private val service: ProductItemService, private val orderService: OrderService) {

    @PostMapping
    fun addItem(@RequestBody item: @Valid ProductItem, @Parameter(hidden = true) session: HttpSession): ProductItem? {
        val id = session.getAttribute(SessionObject.SESSION_ORDER.name) as Long
        val order: Order? = orderService.getOrder(id)
        return service.addItem(order, item)
    }

    @DeleteMapping("{id}")
    fun removeItem(@PathVariable id: Long, @Parameter(hidden = true) session: HttpSession): ProductItem? {
        val removedItem: ProductItem? = service.removeItem(id)
        orderService.removeItem(session.getAttribute(SessionObject.SESSION_ORDER.name) as Long, removedItem)
        return removedItem
    }

    @PutMapping("{id}")
    fun updateItem(@PathVariable id: Long, @RequestBody item: ProductItem): ProductItem? {
        return service.updateItem(id, item)
    }
}