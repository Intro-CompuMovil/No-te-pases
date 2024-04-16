package com.example.notepases1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.example.notepases1.databinding.ActivityParadasBinding
import com.example.notepases1.databinding.ActivityRutaParaderoBinding
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import java.nio.charset.Charset

class Paradas : AppCompatActivity() {
    private lateinit var bindingParadas: ActivityParadasBinding
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    /*private lateinit var sensorManejador: SensorManager
    private lateinit var sensorLuz: Sensor
    private lateinit var sensorLuzListener: SensorEventListener*/

    override fun onCreate(savedInstanceState: Bundle?) {
        /*sensorManejador = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorLuz = sensorManejador.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        sensorLuzListener = createLightSensorListener()
        sensorManejador.registerListener(
            sensorLuzListener,
            sensorLuz,
            SensorManager.SENSOR_DELAY_FASTEST
        )*/

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paradas)
        bindingParadas = ActivityParadasBinding.inflate(layoutInflater)
        setContentView(bindingParadas.root)

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

    /*private fun createLightSensorListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (bindingParadas.osmMap != null) {
                    if (event.values[0] < 3000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0])
                        bindingParadas.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0])
                        bindingParadas.osmMap.overlayManager.tilesOverlay.setColorFilter(null)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
    }*/

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
            val userLocation = GeoPoint(4.6277777777778, -74.065)
            val mapController = mapView.controller
            mapController.setCenter(userLocation)
            mapController.setZoom(20)

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
