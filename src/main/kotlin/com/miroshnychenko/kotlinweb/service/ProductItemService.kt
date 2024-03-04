package com.miroshnychenko.kotlinweb.service

import com.miroshnychenko.kotlinweb.entity.Order
import com.miroshnychenko.kotlinweb.entity.Product
import com.miroshnychenko.kotlinweb.entity.ProductItem
import com.miroshnychenko.kotlinweb.repository.ProductItemRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Service
class ProductItemService(private val repository: ProductItemRepository, private val productService: ProductService) {

    fun checkoutItem(item: ProductItem) {
        val product: Product? = item.product
        val productQuantity: Int = item.quantity
        if (product != null && product.available > productQuantity) {
            productService.removeItems(product, productQuantity)
        }
    }

    fun addItem(order: Order, item: ProductItem): ProductItem? {
        val product: Product? = item.product
        if (product != null && product.available >= item.quantity) {
            calculateItemPrice(item)
            item.order = order
            val savedItem: ProductItem = repository.save(item)
            var items: MutableList<ProductItem?>? = order.items
            if (items == null) {
                items = ArrayList<ProductItem?>()
                order.items = items
            }
            items.add(savedItem)
            return savedItem
        }
        return null
    }

    //Visible for testing
    fun calculateItemPrice(item: ProductItem) {
        item.price = BigDecimal.valueOf(item.product!!.price * item.quantity).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun removeItem(id: Long?): ProductItem? {
        return repository.removeById(id)
    }

    fun updateItem(id: Long?, item: ProductItem): ProductItem? {
        if (repository.findByIdOrNull(id!!) != null) {
            item.id = id
            calculateItemPrice(item)
            return repository.save(item)
        }
        return null
    }
}