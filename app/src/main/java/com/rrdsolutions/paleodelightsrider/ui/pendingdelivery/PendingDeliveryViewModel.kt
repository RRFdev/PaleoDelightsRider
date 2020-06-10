package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.rrdsolutions.paleodelightsrider.Order


@Suppress("UNCHECKED_CAST")
class PendingDeliveryViewModel : ViewModel() {

    var index = 0
    lateinit var username: String
    lateinit var pendingdelivery:MutableList<String>
    var pendingorderlist = arrayListOf<Order>()

    val coordinate = MutableLiveData<LatLng>().apply{
        value = LatLng(0.0,0.0)
    }

    fun queryRider(callback:(String)->Unit){
        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("customer orders")
            .whereEqualTo("rider", "")
            .whereEqualTo("status", "IN PROGRESS")

        db.get()
            .addOnSuccessListener{documents->
                if (documents.size() == 0) callback("No Delivery")
                else{
                    pendingorderlist = arrayListOf<Order>()
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
                        pendingorderlist.add(order)
                    }
                    callback("Delivery Present")
                }
            }
            .addOnFailureListener{
                callback("No Connection")
            }
    }

    fun updateDelivery(number:String, callback:(Boolean)->Unit){
        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
        db.collection("customer orders").document(number)
            .update("rider", username)
            .addOnSuccessListener{
                pendingorderlist = arrayListOf()
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }
    }

}