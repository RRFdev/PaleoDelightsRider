package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CurrentDeliveryViewModel : ViewModel() {

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

    fun queryFirebase(phonenumber: String, callback:(Boolean)->Unit){
        Log.d("_delivery", "Loading current orders")

        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .whereEqualTo("phonenumber", phonenumber)
            .whereEqualTo("status", "IN PROGRESS")
    }

}