package com.rrdsolutions.paleodelightsrider.ui.log

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LogViewModel : ViewModel() {
    var visibility = MutableLiveData<Int>().apply{ value = View.GONE }

    fun loginWith(username: String, password:String, callback:(String)-> Unit){
        val db = FirebaseFirestore
            .getInstance().collection("riders")

        db.document(username).get()
            .addOnSuccessListener{ document->

                if (document.exists()){
                    val realpassword = document.data?.get("password") as String
                    Log.d("_login", "realpassword = $realpassword")

                    if (realpassword == password){
                        callback("login success")
                    }
                    else{
                        callback("password incorrect")
                    }
                }
                else callback ("username incorrect")

            }
            .addOnFailureListener{
                callback("login fail")
            }
    }
}