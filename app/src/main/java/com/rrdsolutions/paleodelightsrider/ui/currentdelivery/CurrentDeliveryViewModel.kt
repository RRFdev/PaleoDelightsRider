package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.rrdsolutions.paleodelightsrider.Order


class CurrentDeliveryViewModel : ViewModel() {
    var index = 0
    lateinit var username: String
    var currentorderlist = arrayListOf<Order>()

    fun queryDelivery(callback:(String)->Unit){

        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("customer orders")
            .whereEqualTo("rider", username)
        db.get()
            .addOnSuccessListener{ documents->
                Log.d("_currentdelivery", "size = "+documents.size())
                if (documents.size() == 0) callback("No Delivery")
                else{
                    currentorderlist = arrayListOf<Order>()
                    for (document in documents){
                        val order = Order(
                            document.id,
                            document.data["phonenumber"] as String,
                            document.data["time"] as String,
                            document.data["eta"] as String,
                            document.data["itemlist"] as List<String>,
                            document.data["address"] as String,
                            document.data["status"] as String
                        )
                        currentorderlist.add(order)
                    }

                    callback("Delivery Present")
                }

            }
            .addOnFailureListener{
                callback("No Connection")
            }

    }

    fun updateDelivery(status:String, number:String, callback:(Boolean)->Unit){
        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("customer orders").document(number)

        db.run{
            update("status", status)
            update("rider", "")
        }
            .addOnSuccessListener{
                currentorderlist = arrayListOf()
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }

    }

}