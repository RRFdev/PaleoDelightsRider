package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CurrentDeliveryViewModel : ViewModel() {

    lateinit var username: String



    fun queryRider(callback: (String)->Unit){
        Log.d("_currentdelivery", "Querying rider $username")

        val db = FirebaseFirestore.getInstance()
            .collection("riders").document(username)

        db.get()
            .addOnSuccessListener{

                val currentdelivery = it.get("currentdelivery") as String
                Log.d("_currentdelivery", "currentdelivery = $currentdelivery")

                if (currentdelivery == ""){
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