package com.gka.codesample.randomdestination

import com.google.android.gms.maps.model.LatLng
import kotlin.math.cos
import kotlin.math.sin

class PointsUtils {

    private val EARTH_RADIUS = 6_371_000

    private fun getPoint(center: LatLng, radius: Int, angle: Double): LatLng {
        val east = radius * cos(angle)
        val north = radius * sin(angle)
        val cLat = center.latitude
        val cLng = center.longitude
        val latRadius: Double =
            EARTH_RADIUS * cos(cLat / 180 * Math.PI)
        val newLat = cLat + north / EARTH_RADIUS / Math.PI * 180
        val newLng = cLng + east / latRadius / Math.PI * 180
        return LatLng(newLat, newLng)
    }

    fun generatePointsInRadius(center: LatLng?, radius: Int = 10_000): MutableList<LatLng> {
        val points: MutableList<LatLng> = ArrayList()
        val totalPoints = 60

        for (i in 0 until totalPoints) {
            points.add(getPoint(center!!, radius, i * 2 * Math.PI / totalPoints))
        }

        return points
    }
}