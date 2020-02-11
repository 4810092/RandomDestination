package com.gka.codesample.randomdestination.ui

import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gka.codesample.randomdestination.ActionEnums
import com.gka.codesample.randomdestination.ActionEnums.*
import com.gka.codesample.randomdestination.LocationLiveData
import com.gka.codesample.randomdestination.LocationPermissionsHelper.isLocationPermissionsGranted
import com.gka.codesample.randomdestination.PointsUtils
import com.gka.codesample.randomdestination.R
import com.gka.codesample.randomdestination.api.ApiRepository
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    val progressLiveData = MutableLiveData<Boolean>()

    val locationLiveData = LocationLiveData(application)

    val actionLiveData = MutableLiveData<Pair<ActionEnums, Int>>()

    val pathLiveData = MutableLiveData<MutableList<List<LatLng>>>()

    private val repository = ApiRepository


    fun onMyLocationRefreshClicked() {
        onMapReady()
    }

    fun onMapReady() {
        actionLiveData.value = Pair(CLEAR_MAP, 0)
        if (isLocationPermissionsGranted(getApplication())) {
            if (isGpsEnabled()) {
                progressLiveData.value = true
                locationLiveData.startRequestGeoLocation()
            } else {
                actionLiveData.value = Pair(GPS_UNAVAILABLE, 0)
            }
        } else {
            actionLiveData.value = Pair(REQUEST_LOCATION_PERMISSION, 0)
        }
    }

    fun getRandomPointIn10km(): LatLng {
        val points = PointsUtils().generatePointsInRadius(locationLiveData.value)
        return points.random()
    }

    fun getRoute(pointA: LatLng, pointB: LatLng) {

        viewModelScope.launch {
            try {
                val routeRes = repository.getRoute(
                    "${pointA.latitude},${pointA.longitude}",
                    "${pointB.latitude},${pointB.longitude}",
                    getApplication<Application>().getString(R.string.google_maps_key)
                )
                val path: MutableList<List<LatLng>> = ArrayList()

                if (routeRes.isSuccessful) {

                    val steps =
                        routeRes.body()?.routes?.firstOrNull()?.legs?.firstOrNull()?.steps

                    steps?.let {

                        it.forEach {

                            it.polyline?.points?.let {
                                path.add(PolyUtil.decode(it))
                            }
                        }

                        pathLiveData.postValue(path)


                    } ?: kotlin.run {
                        actionLiveData.postValue(Pair(SHOW_TOAST, R.string.route_unavailable))
                    }

                } else {
                    actionLiveData.postValue(Pair(SHOW_TOAST, R.string.route_error))
                }

            } catch (e: UnknownHostException) {
                actionLiveData.postValue(Pair(SHOW_TOAST, R.string.internet_unavailable))
            } catch (e: Exception) {
                actionLiveData.postValue(Pair(SHOW_TOAST, R.string.error_unknown))
            } finally {
                progressLiveData.postValue(false)
            }
        }
    }

    private fun isGpsEnabled(): Boolean {
        val lm =
            getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}