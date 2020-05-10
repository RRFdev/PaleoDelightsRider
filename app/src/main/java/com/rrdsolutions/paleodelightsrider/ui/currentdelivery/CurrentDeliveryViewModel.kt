package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField

class CurrentDeliveryViewModel : ViewModel() {

    lateinit var username: String



    fun queryRider(callback: (String)->Unit){
        Log.d("_currentdelivery", "Querying rider $username")

        val db = FirebaseFirestore.getInstance()
            .collection("riders").document(username)

        db.get()
            .addOnSuccessListener{
                //val currentdelivery: List<String>

                //val currentdelivery = it.get("currentdelivery") as String
                //currentdelivery = it.data?.get("current delivery") as List<String>
                val currentdelivery = it.data?.get("currentdelivery") as MutableList<String>

                val size = currentdelivery.size
                Log.d("_currentdelivery", "currentdelivery.size = $size")

                if (size == 0){
                    callback("No Delivery")
                }
                else{

                    callback("Delivery Present")
                }


            }
            .addOnFailureListener{
                callback ("No Connection")
            }




    }

}