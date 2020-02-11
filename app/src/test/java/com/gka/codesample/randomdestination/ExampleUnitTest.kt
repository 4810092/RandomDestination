package com.gka.codesample.randomdestination

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun checkPointsUtils() {

        val pointsUtils = PointsUtils()

        val center = LatLng(0.0, 0.0)
        val radius = 10_000


        val points = pointsUtils.generatePointsInRadius(center, radius)

        points.forEach {
            assert(getDistance(center, it) in 9_500.0..10_500.0)
        }
    }


    private fun getDistance(
        pointA: LatLng, pointB: LatLng
    ): Double {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians(pointB.latitude - pointA.latitude)
        val lngDiff = Math.toRadians(pointB.longitude - pointA.longitude)
        val a = (sin(latDiff / 2) * sin(latDiff / 2)
                + (cos(Math.toRadians(pointA.latitude))
                * cos(Math.toRadians(pointB.latitude))
                * sin(lngDiff / 2) * Math.sin(lngDiff / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = earthRadius * c
        val meterConversion = 1609
        return distance * meterConversion
    }
}
