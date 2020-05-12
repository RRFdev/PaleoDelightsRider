package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.maps.android.PolyUtil
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

    val api = "AIzaSyDOBelSygRLsScFP6fPNiL_xvtXQZZIbG8"
    val api2 = "AIzaSyCCIsUDaWJNPdoszn_84jqV4Y3i4vn-gOA"

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

    fun fetch_url(url: String): String {
        return URL(url).readText()
    }

    fun fetch2(url:String, callback: (String)->Unit){
        callback (URL(url).readText())
    }

    suspend fun fetch(url: String): String = withContext(Dispatchers.IO){

        val result = URL(url).readText()
        withContext(Dispatchers.Default){
            result
        }

    }



}