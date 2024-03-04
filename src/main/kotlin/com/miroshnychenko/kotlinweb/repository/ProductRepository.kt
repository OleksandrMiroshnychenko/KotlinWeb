package com.miroshnychenko.kotlinweb.repository

import com.miroshnychenko.kotlinweb.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product?, Long?>
