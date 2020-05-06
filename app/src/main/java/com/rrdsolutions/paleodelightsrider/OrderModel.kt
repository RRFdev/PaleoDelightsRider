package com.rrdsolutions.paleodelightsrider

object OrderModel {

    data class Order(
        var number: String,
        var phonenumber: String,
        var time: String,
        var eta: String,
        var itemlist: List<String>,
        var address:String,
        var status: String
    )
    lateinit var order:Order

    var pendingorderlist = arrayListOf<OrderModel.Order>()
    lateinit var currentorder:Order

}