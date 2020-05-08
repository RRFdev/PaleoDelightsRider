package com.rrdsolutions.paleodelightsrider.ui.Verify

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class VerifyViewModel : ViewModel() {

    lateinit var coordinate: LatLng
    var index = 0

}