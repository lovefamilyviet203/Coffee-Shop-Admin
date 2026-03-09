package com.example.coffeeshopadmin.domain

data class OrderModel(
    var orderId: String = "",
    var userEmail: String = "",
    var userName: String = "",
    var userFcmToken: String = "",
    var items: List<ItemsModel> = emptyList(),
    var totalFee: Double = 0.0,
    var deliveryFee: Double = 10.0,
    var tax: Double = 0.0,
    var grandTotal: Double = 0.0,
    var address: String = "",
    var paymentMethod: String = "",
    var dateTime: String = "",
    var status: String = "Pending",
    var timestamp: Long = 0L
)
