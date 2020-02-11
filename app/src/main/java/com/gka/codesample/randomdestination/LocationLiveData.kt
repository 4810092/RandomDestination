package com.gka.codesample.randomdestination

import android.content.Context
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationLiveData(private val context: Context) : LiveData<LatLng>() {

    private val locationRequest: LocationRequest
        get() {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 1000
            mLocationRequest.fastestInterval = 900
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return mLocationRequest
        }

    fun startRequestGeoLocation() {
        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    fun stopUsingGPS() {
        LocationServices.getFusedLocationProviderClient(context)
            .removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation
            if (location != null) {
                postValue(LatLng(location.latitude, location.longitude))
                stopUsingGPS()
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        stopUsingGPS()
    }
}