package com.gka.codesample.randomdestination.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.gka.codesample.randomdestination.ActionEnums.*
import com.gka.codesample.randomdestination.LocationPermissionsHelper.isLocationPermissionsGranted
import com.gka.codesample.randomdestination.LocationPermissionsHelper.requestPermissions
import com.gka.codesample.randomdestination.LocationPermissionsHelper.shouldShowRequestPermissionRationale
import com.gka.codesample.randomdestination.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.fabric.sdk.android.Fabric
import kotlin.math.max
import kotlin.math.min


class MapsActivity : BaseActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private val permissionRequestCode = 10
    private val settingsRequestCode = 20
    private lateinit var viewModel: MapsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)

        Fabric.with(this, Crashlytics(), Answers())
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObservers()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh)
            viewModel.onMyLocationRefreshClicked()

        return super.onOptionsItemSelected(item)
    }


    private fun setupObservers() {
        viewModel.progressLiveData.observe(this,
            Observer { if (it) showProgress() else hideProgress() })

        viewModel.locationLiveData.observe(this, Observer { showOnMap(it) })

        viewModel.actionLiveData.observe(this, Observer {
            when (it.first) {
                REQUEST_LOCATION_PERMISSION -> requestLocationPermissions()
                CLEAR_MAP -> mMap.clear()
                SHOW_TOAST -> showToast(it.second)
                GPS_UNAVAILABLE -> showNoGpsMessage()
            }
        })

        viewModel.pathLiveData.observe(this, Observer { it ->
            it.forEach {
                mMap.addPolyline(
                    PolylineOptions().addAll(it).color(Color.GREEN).endCap(RoundCap()).width(5F)
                )
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.onMapReady()
    }

    private fun showOnMap(myLatLng: LatLng) {

        mMap.addMarker(MarkerOptions().position(myLatLng))

        val randomPoint = viewModel.getRandomPointIn10km()

        mMap.addMarker(MarkerOptions().position(randomPoint))

        val south =
            LatLng(
                max(myLatLng.latitude, randomPoint.latitude),
                max(myLatLng.longitude, randomPoint.longitude)
            )
        val north =
            LatLng(
                min(myLatLng.latitude, randomPoint.latitude),
                min(myLatLng.longitude, randomPoint.longitude)
            )

        val bound = LatLngBounds(north, south)

        val width = resources.displayMetrics.widthPixels;
        val height = resources.displayMetrics.heightPixels;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, 200))

        viewModel.getRoute(myLatLng, randomPoint)


    }


    private fun requestLocationPermissions() {
        if (shouldShowRequestPermissionRationale(this))
            showRationaleMessage()
        else {
            requestPermissions(this, permissionRequestCode)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode)
            if (isLocationPermissionsGranted(this))
                viewModel.onMyLocationRefreshClicked()
            else if (!shouldShowRequestPermissionRationale(this))
                showRationaleMessageForSettings()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == settingsRequestCode) {
            if (isLocationPermissionsGranted(this)) {
                viewModel.onMyLocationRefreshClicked()
            }
        }
    }


    private fun showRationaleMessage() =
        showMessage(R.string.message_to_request_again) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            requestPermissions(this, permissionRequestCode)
        }


    private fun showRationaleMessageForSettings() =
        showMessage(R.string.message_to_settings) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)//todo
            intent.data = uri

            if (intent.resolveActivity(packageManager) != null)
                startActivityForResult(intent, settingsRequestCode)
        }

    private fun showNoGpsMessage() {
        showMessage(R.string.gps_unavailable) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }
    }

}
