package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.QResult
import com.rrdsolutions.paleodelightsrider.R

import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*

import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_currentdelivery.address
import kotlinx.android.synthetic.main.fragment_currentdelivery.cardView
import kotlinx.android.synthetic.main.fragment_currentdelivery.dotbutton
import kotlinx.android.synthetic.main.fragment_currentdelivery.expandimage
import kotlinx.android.synthetic.main.fragment_currentdelivery.hiddenlayout
import kotlinx.android.synthetic.main.fragment_currentdelivery.leftbutton
import kotlinx.android.synthetic.main.fragment_currentdelivery.mainlayout
import kotlinx.android.synthetic.main.fragment_currentdelivery.menuitemsholder
import kotlinx.android.synthetic.main.fragment_currentdelivery.notificationlayout
import kotlinx.android.synthetic.main.fragment_currentdelivery.notificationtext
import kotlinx.android.synthetic.main.fragment_currentdelivery.number
import kotlinx.android.synthetic.main.fragment_currentdelivery.phonebtn
import kotlinx.android.synthetic.main.fragment_currentdelivery.phonenumber
import kotlinx.android.synthetic.main.fragment_currentdelivery.rightbutton
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*

class CurrentDeliveryFragment : Fragment(), OnMapReadyCallback {

    private lateinit var vm: CurrentDeliveryViewModel
    lateinit var map: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var destiLoc:LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Current Delivery"
        vm = ViewModelProvider(this).get(CurrentDeliveryViewModel::class.java)

        return inflater.inflate(R.layout.fragment_currentdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.setUsernameValue(activity?.intent?.getStringExtra("username") as String)

        vm.setIndexValue(vm.index.value!!)

        vm.queryDelivery{ callback->
            when (callback){
                QResult.DELIVERY_PRESENT->{
                    if (getView()!=null){
                        vm.index.observe(viewLifecycleOwner){
                            if (vm.list.size>0){
                                loadCurrentDelivery(it)
                            }
                        }
                    }
                }
                QResult.NO_DELIVERY->loadNoDelivery("No deliveries at the moment")
                QResult.NO_CONNECTION->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }
        }

        leftbutton.setOnClickListener{
            vm.decreaseIndex()
        }

        rightbutton.setOnClickListener{
            vm.increaseIndex()
        }

        dotbutton.setOnClickListener{

            fun menuClicked(i:Int):MenuItem.OnMenuItemClickListener{
                val onclick = MenuItem.OnMenuItemClickListener {
                    when (it.title){
                        vm.list[i].number->{
                            vm.setIndexValue(i)
                        }
                    }
                    true
                }
                return onclick
            }
            val popupMenu = PopupMenu(requireContext(),dotbutton)

            for (i in 0 until vm.list.size){
                popupMenu.menu
                    .add(vm.list[i].number)
                    .setOnMenuItemClickListener(menuClicked(i))
            }
            popupMenu.show()
        }

        deliverbtn.setOnClickListener{
            val DELIVERED = "DELIVERED"
            val ordernumber = vm.list[vm.index.value!!].number

            vm.updateDelivery(DELIVERED, ordernumber){taskCompleted->
                if (taskCompleted){
                    Toast.makeText(this.context as Activity, ordernumber + " delivered", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(this.context as Activity, "Delivery update failed. Please check your online connection and retry.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancelbtn.setOnClickListener{
            val CANCELED = "CANCELED"
            val ordernumber = vm.list[vm.index.value!!].number

            vm.updateDelivery(CANCELED, ordernumber){taskCompleted->

                if (taskCompleted){
                    Toast.makeText(this.context as Activity, ordernumber + " canceled", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(this.context as Activity, "Delivery update failed. Please check your online connection and retry.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mainlayout.visibility = View.GONE
        notificationlayout.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        //gets rid of Parcel: unable to marshal value errors
        vm.setListValue(ArrayList())
    }

    fun loadNoDelivery(text:String){
        if (mainlayout!=null && notificationlayout!=null) {
            notificationtext.text = text

            mainlayout.visibility = View.GONE
            notificationlayout.visibility = View.VISIBLE
        }
    }

    fun loadCurrentDelivery(i:Int){
        if (mainlayout!=null && notificationlayout!=null) {
            mainlayout.visibility = View.VISIBLE
            notificationlayout.visibility = View.GONE

            val order = vm.list[i]

            number.text = order.number

            menuitemsholder.removeAllViews()
            for (u in 0 until order.itemlist.size){
                val menuitemstext = layoutInflater.inflate(R.layout.menuitemstext, null)
                menuitemstext.desc.text = order.itemlist[u]
                menuitemsholder.addView(menuitemstext)
            }
            address.text = order.address
            phonenumber.text = order.phonenumber
            phonebtn.setOnClickListener{
                callCustomerDialog(i)
            }

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

            getCoordinate(order.address){
                destiLoc = it
                val mapFragment =childFragmentManager.findFragmentById(R.id.cmap) as SupportMapFragment
                mapFragment.getMapAsync(this)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)
            }

            mainlayout.visibility = View.VISIBLE
            notificationlayout.visibility = View.GONE
        }
    }

    fun getCoordinate(address:String, callback:(LatLng)->Unit){
        if (Geocoder.isPresent()){
            val location = Geocoder(this.context as Activity)
                .getFromLocationName(address, 1)
            val coordinate = LatLng (location[0].latitude, location[0].longitude)
            callback(coordinate)
        }
        else{
            val coordinate = LatLng(0.0,0.0)
            callback(coordinate)
        }

    }

    fun callCustomerDialog(i:Int){
        fun makePhoneCall(phonenumber:String){

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + phonenumber)
            startActivity(callIntent)
        }

        val phonenumber = vm.list[i].phonenumber
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

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!
        map.uiSettings.isZoomControlsEnabled = true
        map.clear()

        if (ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            return
        }
        else {
            map.isMyLocationEnabled = true
            //adds rider location
            fun startLocationUpdates() {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        for (it in locationResult.locations){
                            // Update UI with location data
                            val userLoc = LatLng(it.latitude, it.longitude)
                            val dLabel = "Delivery location"
                            map.addMarker(MarkerOptions().position(destiLoc).title(dLabel)).showInfoWindow()

                            val builder = LatLngBounds.Builder().apply{
                                include(userLoc)
                                include(destiLoc)
                            }.build()
                            val cameraupdate = CameraUpdateFactory.newLatLngBounds(builder, 50)
                            map.animateCamera(cameraupdate)
                        }
                    }
                }
                val locationRequest = LocationRequest.create().apply{
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = (10 * 1000).toLong()
                }

                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())

            }
            startLocationUpdates()
        }

    }

}



