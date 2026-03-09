package com.example.coffeeshopadmin.domain

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: List<String> = emptyList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = "",
    var categoryId: String = ""
)
