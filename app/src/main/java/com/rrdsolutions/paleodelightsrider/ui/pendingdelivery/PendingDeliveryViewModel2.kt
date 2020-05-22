package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.rrdsolutions.paleodelightsrider.OrderModel

@Suppress("UNCHECKED_CAST")
class PendingDeliveryViewModel2 : ViewModel() {
    var index = 0
    lateinit var username: String
    lateinit var pendingdelivery:MutableList<String>
    var pendingorderlist = arrayListOf<OrderModel.Order>()

    fun queryRider(callback:(String)->Unit){
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .whereEqualTo("rider", "")
            .whereEqualTo("status", "IN PROGRESS")

        db.get()
            .addOnSuccessListener{documents->
                if (documents.size() == 0) callback("No Delivery")
                else{
                    pendingorderlist = arrayListOf<OrderModel.Order>()
                    for (document in documents){
                        val order = OrderModel.Order(
                            document.id,
                            document.data["phonenumber"] as String,
                            document.data["time"] as String,
                            document.data["eta"] as String,
                            document.data["itemlist"] as List<String>,
                            document.data["address"] as String,
                            document.data["status"] as String
                        )
                        pendingorderlist.add(order)
                    }
                    callback("Delivery Present")
                }
            }
            .addOnFailureListener{
                callback("No Connection")
            }
    }

}