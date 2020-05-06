package com.rrdsolutions.paleodelightsrider.ui.verifydelivery

import android.app.Activity
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class VerifyDeliveryViewModel: ViewModel() {

    lateinit var coordinate:LatLng
    var index = 0


}