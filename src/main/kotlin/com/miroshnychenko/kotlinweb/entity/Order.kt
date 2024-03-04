package com.miroshnychenko.kotlinweb.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "gd_order")
data class Order(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "price")
        var price: Double = 0.0,

        @Enumerated(EnumType.ORDINAL)
        var status: OrderStatus = OrderStatus.CREATED,

        @OneToOne
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        var user: User? = null,

        @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JsonManagedReference("order-items")
        var items: MutableList<ProductItem?>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val order: Order = other as Order

        return id == order.id
    }

    override fun hashCode(): Int {
        return if (id != null) id.hashCode() else 0
    }

    override fun toString(): String {
        return "Order{" +
                "id=" + id +
                '}'
    }
}