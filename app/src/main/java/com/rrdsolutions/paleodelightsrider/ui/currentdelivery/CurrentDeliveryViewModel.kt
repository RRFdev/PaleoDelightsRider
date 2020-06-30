package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.rrdsolutions.paleodelightsrider.Order
import com.rrdsolutions.paleodelightsrider.QResult

class CurrentDeliveryViewModel(private val ssh: SavedStateHandle) : ViewModel() {

    var index = MutableLiveData<Int>().apply{value = 0}
    var username = ""
    var list = arrayListOf<Order>()

    fun setIndexValue(i:Int){
        val INDEX = "index"
        ssh.set(INDEX, i)
        index = ssh.getLiveData<Int>(INDEX)
    }

    fun setUsernameValue(i: String){
        val USERNAME = "username"
        ssh.set(USERNAME, i)
        username = ssh.get<String>(USERNAME)!!
    }

    fun setListValue(i:ArrayList<Order>){
        val LIST = "list"
        ssh.set(LIST, i)
        list = ssh.get<ArrayList<Order>>(LIST)!!
    }

    fun decreaseIndex(){
        var index = index.value!!

        if (index != 0) {
            index--
        }
        else {
            index = list.size-1
        }
        setIndexValue(index)
    }

    fun increaseIndex(){
        var index = index.value!!
        if (index == (list.size-1)){
            index = 0
        }
        else {
            index++
        }
        setIndexValue(index)
    }

    fun queryDelivery(callback:(QResult)->Unit){

        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("customer orders")
            .whereEqualTo("rider", username)

        db.addSnapshotListener { snapshot, e ->

            if (e != null) {
                callback(QResult.NO_CONNECTION)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {

                val list2 = arrayListOf<Order>()

                for (document in snapshot){
                    val order = Order(
                        document.id,
                        document.data["phonenumber"] as String,
                        document.data["time"] as String,
                        document.data["eta"] as String,
                        document.data["itemlist"] as List<String>,
                        document.data["address"] as String,
                        document.data["status"] as String
                    )
                    list2.add(order)
                }

                setListValue(list2)
                callback(QResult.DELIVERY_PRESENT)

            }
            else {
                callback(QResult.NO_DELIVERY)
            }
        }

    }

    fun updateDelivery(status:String, number:String, callback:(Boolean)->Unit){

        val tempindex = index.value!!
        setIndexValue(0)

        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("customer orders").document(number)

        db.run{
            update("status", status)
            update("rider", "")
        }
            .addOnSuccessListener{
                callback(true)
            }
            .addOnFailureListener{
                setIndexValue(tempindex)
                callback(false)
            }
    }

}

