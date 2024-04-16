package com.example.notepases1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import java.nio.charset.Charset

class Paradas : AppCompatActivity() {
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paradas)

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        mapView = findViewById(R.id.map)
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            showCurrentLocation()
            loadMarkers()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation()
            loadMarkers()
        }
    }

    @SuppressLint("MissingPermission", "UseCompatLoadingForDrawables")
    private fun showCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
            val userLocation = GeoPoint(location.latitude, location.longitude)
            val mapController = mapView.controller
            mapController.setCenter(userLocation)

            val marker = Marker(mapView)
            marker.position = userLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = getResources().getDrawable(R.drawable.ic_marker_red)
            mapView.overlays.add(marker)
        }
    }

    private fun loadMarkers() {
        val jsonStr = assets.open("marcadores.json").readBytes().toString(Charset.defaultCharset())
        val jsonArray = JSONObject(jsonStr).getJSONArray("paradas")
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val lat = jsonObject.getDouble("lat")
            val lon = jsonObject.getDouble("lon")
            val nombre = jsonObject.getString("nombre")
            val buses = jsonObject.getString("buses")

            val geoPoint = GeoPoint(lat, lon)
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.title = nombre
            marker.snippet = "Buses: $buses"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.infoWindow = BasicInfoWindow(R.layout.custom_infowindow, mapView)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

}
