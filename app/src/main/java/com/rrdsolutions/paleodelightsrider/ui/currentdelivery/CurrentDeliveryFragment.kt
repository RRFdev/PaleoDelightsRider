package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.rrdsolutions.paleodelightsrider.R

import kotlinx.android.synthetic.main.fragment_currentdelivery.*

import kotlinx.android.synthetic.main.notificationcard.view.*

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
        layout.addView(currentdeliverylayout)


//        val mapFragment = childFragmentManager.findFragmentById(R.id.cmap) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)
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
//        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
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
//        map = p0!!
//        //declare this Fragment as target when user clicks marker
//        map.uiSettings.isZoomControlsEnabled = true
//        map.setOnMarkerClickListener(this)
//
//        if (ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//
//            Log.d("maptest", "User permission not granted")
//
//            return
//        }
//        //**actual code starts here*//
//        else {
//            Log.d("maptest", "User permission granted")
//            map.isMyLocationEnabled = true
//            fusedLocationClient.lastLocation.addOnSuccessListener {
//                if (it != null){
//                    lastLocation = it
//                    ////THIS ONE!!!!
//                    val userLoc = LatLng(it.latitude, it.longitude)
//                    Log.d("maptest", "lat = " + it.latitude + " lng + " + it.longitude)
//                    val titleStr = "Your location"
//                    map.addMarker(MarkerOptions().position(userLoc).title(titleStr)).showInfoWindow()
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 18f))
//                    //17 is almost perfect.
//                    Log.d("maptest", "titleStr is " + titleStr)
//
//                }
//            }
//        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }
}