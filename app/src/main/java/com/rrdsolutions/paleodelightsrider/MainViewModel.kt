package com.rrdsolutions.paleodelightsrider

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    var visibility = MutableLiveData<Int>().apply{ value = View.GONE }
}