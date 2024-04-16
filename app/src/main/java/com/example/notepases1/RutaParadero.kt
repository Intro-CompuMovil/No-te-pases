package com.example.notepases1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RutaParadero : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_paradero)
        mapView = findViewById(R.id.osmMap)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        mapController = mapView.controller
        mapController.setZoom(15)

        // Obtén la latitud y longitud desde el Intent
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val destino = GeoPoint(latitud, longitud)

        mapController.setCenter(destino)

        addMarker(destino, "Destino")


        val ubicacionActual = GeoPoint(4.6277777777778, -74.065)


        drawRoute(ubicacionActual, destino)
    }

    private fun addMarker(point: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapView.overlays.add(marker)
    }

    // Método para dibujar la ruta
    private fun drawRoute(start: GeoPoint, destination: GeoPoint) {
        val line = Polyline()
        line.addPoint(start)
        line.addPoint(destination)
        mapView.overlays.add(line)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
