package com.miroshnychenko.kotlinweb.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "gd_user")
data class User(

        @Column(name = "email")
        var email: @NotBlank String? = null,

        @Column(name = "password")
        var password: @NotBlank String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long? = null

    override fun toString(): String {
        return "User{" +
                "id=" + id +
                '}'
    }
}