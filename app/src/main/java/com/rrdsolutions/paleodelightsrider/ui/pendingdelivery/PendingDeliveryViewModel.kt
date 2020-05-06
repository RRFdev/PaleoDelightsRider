package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.rrdsolutions.paleodelightsrider.OrderModel

@Suppress("UNCHECKED_CAST")
class PendingDeliveryViewModel : ViewModel() {

    fun queryPendingDelivery(callback:(String)->Unit){

        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .whereEqualTo("status", "IN PROGRESS")

        db.get()
            .addOnSuccessListener{ documents->

                if (documents.size() == 0) callback("No Delivery")
                else{
                    OrderModel.pendingorderlist = arrayListOf<OrderModel.Order>()
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
                        OrderModel.pendingorderlist.add(order)
                    }

                    callback("Delivery Present")
                }


            }
            .addOnFailureListener{
                callback("No Connection")
            }

    }
}