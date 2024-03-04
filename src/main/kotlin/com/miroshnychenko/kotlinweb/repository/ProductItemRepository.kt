package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.ProductItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductItemRepository : JpaRepository<ProductItem?, Long?> {
    fun removeById(id: Long?): ProductItem?
}