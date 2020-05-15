package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.fragment_currentdelivery_layout.view.*
import kotlinx.android.synthetic.main.menuitemstext.view.*
import kotlinx.android.synthetic.main.notificationcard.view.notificationtext
import java.lang.Math.abs

class CurrentDeliveryFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var vm: CurrentDeliveryViewModel
    lateinit var map: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation: Location
    lateinit var destiLoc:LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Current Delivery"


        return inflater.inflate(R.layout.fragment_currentdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(CurrentDeliveryViewModel::class.java)
        vm.username = activity?.intent?.getStringExtra("username") as String
        Log.d("_currentdelivery", "username = $vm.username")


    }

    override fun onPause(){
        //layout.removeAllViews()
        super.onPause()
    }

    override fun onResume(){
        super.onResume()
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
        layout.removeAllViews()

        vm.queryRider2(){ callback->
            when (callback){
                "Delivery Present"->loadCurrentDelivery(0)
                "No Delivery"->loadNoDelivery("No deliveries at the moment")
                "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }
        }
    }

    fun loadCurrentDelivery(i:Int){

        val currentindex = i

        val currentdeliverylayout = layoutInflater.inflate(R.layout.fragment_currentdelivery_layout, null)
        //fill card details
        val order = vm.currentorderlist[i]


        //number.text = order.number
        currentdeliverylayout.number.text = order.number



        for (u in 0 until order.itemlist.size){
            val menuitemstext = layoutInflater.inflate(R.layout.menuitemstext, null)
            menuitemstext.desc.text = order.itemlist[u]
            currentdeliverylayout.menuitemsholder.addView(menuitemstext)
        }
        currentdeliverylayout.address.text = order.address
        currentdeliverylayout.cardView.setOnClickListener{
            if (currentdeliverylayout.hiddenlayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(
                    currentdeliverylayout.cardView as ViewGroup,
                    AutoTransition()
                )
                currentdeliverylayout.hiddenlayout.visibility = View.VISIBLE
                currentdeliverylayout.expandimage.animate().rotation(180f).start()
            } else {
                TransitionManager.beginDelayedTransition(
                    currentdeliverylayout.cardView as ViewGroup,
                    Fade().setDuration(300)
                )
                currentdeliverylayout.hiddenlayout.visibility = View.GONE
                currentdeliverylayout.expandimage.animate().rotation(0f).start()
            }
        }



//
//        currentdeliverylayout.dotbutton.setOnClickListener{
////            val popupMenu: PopupMenu = PopupMenu(requireContext(),dotbutton)
////
////            val textlist = listOf("haha", "hihi", "huhu")
////            popupMenu.menu.add("haha")
////            popupMenu.menu.add("hihi")
////            popupMenu.menu.add("huhu")
////            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
////                when(item.title) {
////                    "haha" ->{
////
////                    }
////
////                    "hihi" ->{
////
////                    }
////
////                    "huhu" ->{
////
////                    }
////
////                }
////                true
////            })
//        }

        getCoordinate(order.address){
            destiLoc = it
            val mapFragment =childFragmentManager.findFragmentById(R.id.cmap) as SupportMapFragment
            mapFragment.getMapAsync(this)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)
        }

        layout.addView(currentdeliverylayout)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
    }

    fun getCoordinate(address:String, callback:(LatLng)->Unit){
        val location = Geocoder(this.context as Activity)
            .getFromLocationName(address, 1)
        val coordinate = LatLng (location[0].latitude, location[0].longitude)
        callback(coordinate)
    }

    @SuppressLint("SetTextI18n")
    fun loadNoDelivery(text:String){
        layout.removeAllViews()
        val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
        notificationcard.notificationtext.text = text
        layout.addView(notificationcard)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
    }

    override fun onMapReady(p0: GoogleMap?) {
        fun addDestinationMarker(){}
        map = p0!!
        map.uiSettings.isZoomControlsEnabled = true
        //declare this Fragment as target when user clicks marker
        //map.setOnMarkerClickListener(this)
        if (ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.d("maptest", "User permission not granted")

            return
        }
        else {
            Log.d("maptest", "User permission granted")

            map.isMyLocationEnabled = true
            //adds rider location
            fun showLocationOnce(){
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        if (it != null){
                            lastLocation = it
                            val userLoc = LatLng(it.latitude, it.longitude)
                            val titleStr = "Your location"

                            val dLoc = LatLng(3.8032, 103.3241)
                            val dLabel = "Kompleks Teruntum"

                            //map.addMarker(MarkerOptions().position(userLoc).title(titleStr).
                            //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            //.showInfoWindow()
                            //map.addMarker(MarkerOptions().position(dLoc).title(dLabel)).showInfoWindow()
                            map.addMarker(MarkerOptions().position(destiLoc).title(dLabel)).showInfoWindow()

                            val interlat = abs((it.latitude + 3.8032)/2)
                            val interlong = abs((it.longitude + 103.3241)/2)
                            val interLog = LatLng(interlat, interlong)
                            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 13f))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(interLog, 13f))
                            //13 for 10 km distance, higher means zoom in

                        }
                    }
            }
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
                            val cameraupdate = CameraUpdateFactory.newLatLngBounds(builder, 100)
                            map.animateCamera(cameraupdate)


                        }
                    }
                }
                val locationRequest = LocationRequest.create().apply{
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = (10 * 1000).toLong()
                    //fastestInterval = (2 * 1000).toLong()
                }

                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())

            }
            startLocationUpdates()



        }

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }





}