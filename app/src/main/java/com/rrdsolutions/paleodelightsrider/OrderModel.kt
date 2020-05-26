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


}