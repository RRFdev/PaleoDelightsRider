package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

//import kotlinx.android.synthetic.main.fragment_currentdelivery.*

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*


class PendingDeliveryFragment : Fragment(){
    private lateinit var vm: PendingDeliveryViewModel
    lateinit var coordinate: LatLng



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(this).get(PendingDeliveryViewModel::class.java)
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Pending Delivery"




        val root = activity?.layoutInflater?.inflate(R.layout.fragment_pendingdelivery, container, false)
        return root
    }
    override fun onDestroyView(){
        super.onDestroyView()


    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.username = activity?.intent?.getStringExtra("username") as String
        Log.d("_pending", "created")

        map.onCreate(savedInstanceState)
        map.getMapAsync{googleMap->
            vm.coordinate.observe(viewLifecycleOwner, Observer{
                googleMap.addMarker(MarkerOptions().position(it).title("Customer Location")).showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18f))

            })
        }

        //mapFragment = (activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment?)!!
        leftbutton.setOnClickListener{
            //mapholder.visibility = View.VISIBLE
            errorcard.visibility = View.GONE
            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
            if (vm.index != 0) {
                vm.index--
            }
            else {
                vm.index = vm.pendingorderlist.size-1
            }
            loadPendingDelivery(vm.index)
        }

        rightbutton.setOnClickListener{
           // mapholder.visibility = View.VISIBLE
            errorcard.visibility = View.GONE
            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
            if (vm.index == (vm.pendingorderlist.size-1)){
                vm.index = 0

            }
            else {
                vm.index++
            }
            loadPendingDelivery(vm.index)
        }

        dotbutton.setOnClickListener{
            fun menuClicked(i:Int): MenuItem.OnMenuItemClickListener{
                val onclick = MenuItem.OnMenuItemClickListener {
                    when (it.title){
                        vm.pendingorderlist[i].number->{
                            //mapholder.visibility = View.VISIBLE
                            errorcard.visibility = View.GONE
                            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
                            loadPendingDelivery(i)
                        }
                    }
                    true
                }
                return onclick
            }
            val popupMenu = PopupMenu(requireContext(),dotbutton)

            for (i in 0 until vm.pendingorderlist.size){
                popupMenu.menu
                    .add(vm.pendingorderlist[i].number)
                    .setOnMenuItemClickListener(menuClicked(i))
            }
            Log.d("_current", "dot button clicked")
            popupMenu.show()
        }

        pickupbtn.setOnClickListener{
//            if(errorcard.visibility == View.GONE){
//                vm.updateDelivery(vm.pendingorderlist[vm.index].number){callback->
//                    if (callback){
//                        Toast.makeText(this.context as Activity, vm.pendingorderlist[vm.index].number + "marked for delivery", Toast.LENGTH_SHORT).show()
//                        view.let { Navigation.findNavController(it).navigate(R.id.nav_backtocurrent) }
//                    }
//                    else{
//                        //mapholder.visibility = View.GONE
//                        errorcard.visibility = View.VISIBLE
//                        notificationtext2.text = "Delivery update failed. Please check your online connection and retry."
//                    }
//                }
//            }




            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE

        }

        mainlayout.visibility = View.GONE
        notificationlayout.visibility = View.GONE

    }

    override fun onPause(){
        super.onPause()
        //map.onPause()
        //fragmentTransaction.remove(mMapFragment)
        map.onPause()

    }


    override fun onResume() {
        super.onResume()
        map.onResume()
        //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
        vm.queryRider(){callback->
            when (callback){
                "Delivery Present"-> loadPendingDelivery(vm.index)
                "No Delivery"->loadNoDelivery("No deliveries at the moment")
                "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }

        }


    }
    fun loadNoDelivery(text:String){
        mainlayout.visibility = View.GONE
        notificationlayout.visibility = View.VISIBLE
        notificationtext.text = text
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
    }
    fun loadPendingDelivery(i:Int){
        mainlayout.visibility = View.VISIBLE
        notificationlayout.visibility = View.GONE

        val order = vm.pendingorderlist[i]

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

        getCoordinate(i){locationFound->
            if (locationFound){
                //mapholder.visibility = View.VISIBLE
                errorcard.visibility = View.GONE
                Log.d("_pending", "callback requested")
                Log.d("_pending", "2coordinate = " + coordinate)

                //var mapa =  (activity?.supportFragmentManager?.findFragmentById(R.id.mapa) as SupportMapFragment?)



                activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
            }
            else{
                //mapholder.visibility = View.GONE
                errorcard.visibility = View.VISIBLE
                activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
            }

        }


    }



    val callbackCustomerLocation = OnMapReadyCallback { googleMap ->
        Log.d("_pending", "callback requested2")
        googleMap.addMarker(MarkerOptions().position(coordinate).title("Customer Location")).showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18f))
    }

    fun getCoordinate(i: Int, locationFound:(Boolean)->Unit){
        val address = vm.pendingorderlist[i].address
        Log.d("_pending", "address = " + address)
        val location = Geocoder(this.context as Activity)
            .getFromLocationName(address, 1)
        Log.d("_pending", "location.size = " + location.size)
        if (location.size == 0){
            locationFound(false)
        }
        else{

            coordinate = LatLng (location[0].latitude, location[0].longitude)
            vm.coordinate.value = coordinate
            Log.d("_pending", "coordinate = " + coordinate)
            locationFound(true)
        }
        //coordinate = LatLng (0.0, 0.0)
    }

    fun callCustomerDialog(i:Int){
        fun makePhoneCall(phonenumber:String){

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + phonenumber)
            startActivity(callIntent)
        }

        val phonenumber = vm.pendingorderlist[i].phonenumber
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

    fun moveToCurrentDelivery(){

    }




}
