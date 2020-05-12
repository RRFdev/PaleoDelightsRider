package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.execute
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.notificationcard.view.*
import org.json.JSONObject
import java.lang.Math.abs

class CurrentDeliveryFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var vm: CurrentDeliveryViewModel
    lateinit var map: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation: Location

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
        layout.removeAllViews()
        super.onPause()
    }



    override fun onResume(){
        super.onResume()

        vm.queryRider(){ callback->

            when (callback){
                "Delivery Present"->loadCurrentDelivery()
                "No Delivery"->loadNoDelivery("No deliveries at the moment")
                "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }


        }

    }

    fun loadCurrentDelivery(){
        layout.removeAllViews()
//
        val currentdeliverylayout = layoutInflater.inflate(R.layout.fragment_currentdelivery_layout, null)
        val mapFragment =childFragmentManager.findFragmentById(R.id.cmap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)
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

        layout.addView(currentdeliverylayout)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
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
                    map.addMarker(MarkerOptions().position(dLoc).title(dLabel)).showInfoWindow()


                    val interlat = abs((it.latitude + 3.8032)/2)
                    val interlong = abs((it.longitude + 103.3241)/2)
                    val interLog = LatLng(interlat, interlong)
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 13f))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(interLog, 13f))
                    //13 for 10 km distance, higher means zoom in

                }
                }
                .addOnFailureListener{}
        }

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }


    fun getURL(from : LatLng, to : LatLng) : String {
            val origin = "origin=" + from.latitude + "," + from.longitude
            val dest = "destination=" + to.latitude + "," + to.longitude
            val sensor = "sensor=false"
            val params = "$origin&$dest&$sensor"
            return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }
    fun getJson(result:String):JsonObject{
       // val parser: Parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(result)
        val json: JsonObject = Parser().parse(stringBuilder) as JsonObject

        return json
    }


}