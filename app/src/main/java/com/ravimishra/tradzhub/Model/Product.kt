package com.ravimishra.tradzhub.Model

import java.io.Serializable

data class Product(
        val id: Long,
        var name: String,
        var price: Int,
        var discount: Int,
        var imgUrl: String,
        var desc: String,
        var wishlist: String,
        var cart: String
        ):Serializable