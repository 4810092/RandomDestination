package com.gka.codesample.randomdestination

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.*

object LocationPermissionsHelper {

    fun shouldShowRequestPermissionRationale(activity: Activity) =
        shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(activity, ACCESS_COARSE_LOCATION)

    fun requestPermissions(activity: Activity, requestCode: Int) =
        requestPermissions(
            activity,
            arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), requestCode
        )

    fun isLocationPermissionsGranted(context: Context) =
        checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}