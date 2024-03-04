package com.miroshnychenko.kotlinweb.entity

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "gd_product")
data class Product(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "title")
        var title: @NotBlank String? = null,

        @Column(name = "price")
        var price: Double = 0.0,

        @Column(name = "available")
        var available: Int = 0
) {
    override fun toString(): String {
        return "Product{" +
                "id=" + id +
                '}'
    }
}