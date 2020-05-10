package com.rrdsolutions.paleodelightsrider.ui.verifydelivery

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.rrdsolutions.paleodelightsrider.OrderModel

import kotlinx.android.synthetic.main.fragment_verifydelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*

class VerifyDeliveryFragment : Fragment() {

    lateinit var vm: VerifyDeliveryViewModel
    lateinit var coordinate: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Verify Delivery"

        activity?.getPreferences(0)?.edit()?.putBoolean("back", true)?.apply()



        return inflater.inflate(R.layout.fragment_verifydelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(VerifyDeliveryViewModel::class.java)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.VISIBLE
        vm.index = activity?.getPreferences(0)?.getInt("index", 0)!!
        //vm.index = arguments?.getInt("index") as Int

        buildCard(vm.index)

        cardView.setOnClickListener{
            if (hiddenlayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(
                    cardView as ViewGroup,
                    AutoTransition()
                )
                hiddenlayout.visibility = View.VISIBLE

               expandimage.animate().rotation(180f).start()
            } else {
                TransitionManager.beginDelayedTransition(
                    cardView as ViewGroup,
                    Fade().setDuration(300)
                )
                hiddenlayout.visibility = View.GONE

                expandimage.animate().rotation(0f).start()
            }
        }

        getCoordinateFromAddress(vm.index){ locationFound->
            if (locationFound){
                Log.d("_verifydelivery", "location found")
                errorcard.visibility = View.GONE
                //vmap.visibility = View.VISIBLE
                val mapFragment = childFragmentManager.findFragmentById(R.id.vmap) as SupportMapFragment?
                mapFragment?.getMapAsync(callbackCustomerLocation)
            }
            else{
                Log.d("_verifydelivery", "location not found")
                errorcard.visibility = View.VISIBLE
               //vmap.visibi = View.GONE
            }

        }

        pickupbtn.setOnClickListener{
            pickupDelivery(vm.index)
            moveToCurrentDelivery()
        }

        fab.setOnClickListener{
            callCustomerDialog(vm.index)
        }

    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }

    private val callbackCustomerLocation = OnMapReadyCallback { googleMap ->

        val sydney = coordinate
        googleMap.addMarker(MarkerOptions().position(coordinate).title("Customer Location")).showInfoWindow()
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18f))
    }

    fun getCoordinateFromAddress(i: Int, locationFound:(Boolean)->Unit){

        val address = OrderModel.pendingorderlist[i].address

        Log.d("_verifydelivery", "address = $address")
        val location = Geocoder(this.context as Activity).getFromLocationName(address, 1)

        if (location.size == 0){
            Log.d("_verifydelivery", "location not found")
            locationFound(false)
        }
        else{
            coordinate = LatLng (location[0].latitude, location[0].longitude)
            Log.d("_verifydelivery", "location = $location")
            Log.d("_verifydelivery", "coordinate = $coordinate")
            locationFound(true)
        }

    }

    fun buildCard(i: Int){
        val order = OrderModel.pendingorderlist[i]
        number.text = order.number
        for (u in 0 until order.itemlist.size){
            val menuitemstext = layoutInflater.inflate(R.layout.menuitemstext, null)
            menuitemstext.desc.text = order.itemlist[u]
            menuitemsholder.addView(menuitemstext)
        }
        address.text = order.address

    }

    fun callCustomerDialog(i:Int){
        val phonenumber = OrderModel.pendingorderlist[i].phonenumber
        MaterialDialog(this.context as Activity).show {
            title(text = "Call confirmation")
            message(text = "Call customer at $phonenumber directly?")

            positiveButton(text = "Proceed"){ dialog ->
                makePhoneCall(phonenumber)
            }
            negativeButton(text = "Cancel"){ dialog ->
                dismiss()
            }
        }
    }


    fun makePhoneCall(phonenumber:String){

        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + phonenumber)
        startActivity(callIntent)
    }

    fun pickupDelivery(i:Int){

        val number = OrderModel.pendingorderlist[i].number
        val username = activity?.intent?.getStringExtra("username") as String
        val db = FirebaseFirestore.getInstance()

        //update customer order
        db.collection("customer orders").document(number).apply{
            update("rider", username)
            update("status", "IN DELIVERY")
            //default "IN PROGRESS"
        }
        //update rider profile
        db.collection("riders").document(username).get()
            .addOnSuccessListener{
                val currentdelivery = it.data?.get("currentdelivery") as MutableList<String>
                var size = currentdelivery.size
                Log.d("_verify", "currentdelivery.size = $size")
                currentdelivery.add(number)
                size = currentdelivery.size
                Log.d("_verify", "currentdelivery.size after adding = $size")
                db.collection("riders").document(username).update("currentdelivery", currentdelivery)

            }



    }

    fun moveToCurrentDelivery(){


    }


}