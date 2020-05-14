package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.maps.android.PolyUtil
import com.rrdsolutions.paleodelightsrider.OrderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL


class CurrentDeliveryViewModel : ViewModel() {

    lateinit var username: String
    lateinit var currentdelivery:MutableList<String>

    var currentorderlist = arrayListOf<OrderModel.Order>()

    fun queryRider(callback: (String)->Unit){
        Log.d("_currentdelivery", "Querying rider $username")

        val db = FirebaseFirestore.getInstance()
            .collection("riders").document(username)

        db.get()
            .addOnSuccessListener{
                //val currentdelivery: List<String>

                //val currentdelivery = it.get("currentdelivery") as String
                //currentdelivery = it.data?.get("current delivery") as List<String>
                currentdelivery = it.data?.get("currentdelivery") as MutableList<String>

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

    fun queryDelivery(name: String, callback: (String)->Unit){
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders").document(name)

        db.get().addOnSuccessListener{
            val address = it.data?.get("address") as String
            callback(address)
        }
    }

    fun queryRider2(callback:(String)->Unit){
        //fill out currentdelivery
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .whereEqualTo("rider", username)
        db.get()
            .addOnSuccessListener{ documents->

                if (documents.size() == 0) callback("No Delivery")
                else{
                    currentorderlist = arrayListOf<OrderModel.Order>()
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
                        currentorderlist.add(order)
                    }

                    callback("Delivery Present")
                }


            }
            .addOnFailureListener{
                callback("No Connection")
            }




    }

}