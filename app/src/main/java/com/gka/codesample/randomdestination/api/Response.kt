package com.gka.codesample.randomdestination.api

import com.google.gson.annotations.SerializedName


class ResponseRoute {
    @SerializedName("routes")
    var routes: List<Route>? = null
}

class Route {
    @SerializedName("legs")
    var legs: List<Leg>? = null
}

class Leg {
    @SerializedName("steps")
    var steps: List<Step>? = null
}

class Step {
    @SerializedName("polyline")
    var polyline: Polyline? = null
}

class Polyline {
    @SerializedName("points")
    var points: String? = null
}