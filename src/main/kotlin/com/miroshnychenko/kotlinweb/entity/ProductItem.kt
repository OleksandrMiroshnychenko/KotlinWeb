package com.miroshnychenko.kotlinweb.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "gd_product_item")
data class ProductItem(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "quantity")
        var quantity: Int = 0,

        @Column(name = "price")
        var price: Double = 0.0,

        @ManyToOne
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        var product: Product? = null,

        @ManyToOne
        @JoinColumn(name = "order_id", referencedColumnName = "id")
        @JsonBackReference("order-items")
        var order: Order? = null
) {
    override fun toString(): String {
        return "ProductItem{" +
                "id=" + id +
                '}'
    }
}