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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.rrdsolutions.paleodelightsrider.OrderModel
import kotlinx.android.synthetic.main.fragment_verifydelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*

class VerifyDeliveryFragment : Fragment() {

    lateinit var vm: VerifyDeliveryViewModel
    lateinit var coordinate: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Verify Delivery"
        return inflater.inflate(R.layout.fragment_verifydelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(VerifyDeliveryViewModel::class.java)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.VISIBLE

        vm.index = arguments?.getInt("index") as Int

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
                errorlayout.visibility = View.GONE
                vmap.visibility = View.VISIBLE
                val mapFragment = childFragmentManager.findFragmentById(R.id.vmap) as SupportMapFragment?
                mapFragment?.getMapAsync(callbackCustomerLocation)
            }
            else{
                errorlayout.visibility = View.VISIBLE
                vmap.visibility = View.GONE
            }

        }

        pickupbtn.setOnClickListener{

        }


    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }

    private val callbackCustomerLocation = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

    fun callCustomerDialog(){

    }


    fun makePhoneCall(i:Int){
        val phonenumber = OrderModel.pendingorderlist[i].phonenumber

        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + phonenumber)
        startActivity(callIntent)
    }
}