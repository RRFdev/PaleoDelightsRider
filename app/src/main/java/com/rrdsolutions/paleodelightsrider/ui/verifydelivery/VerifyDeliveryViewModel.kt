package com.rrdsolutions.paleodelightsrider.ui.verifydelivery

import android.app.Activity
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.rrdsolutions.paleodelightsrider.OrderModel

class VerifyDeliveryViewModel: ViewModel() {

    lateinit var coordinate:LatLng
    var index = 0

    fun pickupDelivery(i:Int){

//        val number = OrderModel.pendingorderlist[i].number
//        //val username = activity?.intent?.getStringExtra("username") as String
//        val db = FirebaseFirestore.getInstance()
//
//        //update customer order
//        db.collection("customer orders").document(number).apply{
//            update("rider", username)
//            update("status", "IN DELIVERY")
//            //default "IN PROGRESS"
//        }
//        //update rider profile
//        db.collection("riders").document(username).get()
//            .addOnSuccessListener{
//                val currentdelivery = it.data?.get("currentdelivery") as MutableList<String>
//                var size = currentdelivery.size
//                Log.d("_verify", "currentdelivery.size = $size")
//                currentdelivery.add(number)
//                size = currentdelivery.size
//                Log.d("_verify", "currentdelivery.size after adding = $size")
//                db.collection("riders").document(username).update("currentdelivery", currentdelivery)
//
//            }

    }

}