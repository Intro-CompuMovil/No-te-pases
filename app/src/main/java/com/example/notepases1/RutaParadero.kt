package com.example.notepases1

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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


        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val destino = GeoPoint(latitud, longitud)

        mapController.setCenter(destino)

        marcadorDestino(destino, "Destino")


        val ubicacionActual = GeoPoint(4.6277777777778, -74.065)
        cambiarIcono(ubicacionActual)

        dibujarRuta(ubicacionActual, destino)
    }

    private fun marcadorDestino(point: GeoPoint, titulo: String) {
        val marcador = Marker(mapView)
        marcador.position = point
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = titulo
        mapView.overlays.add(marcador)
    }


    private fun dibujarRuta(start: GeoPoint, destination: GeoPoint) {
        val line = Polyline()
        line.addPoint(start)
        line.addPoint(destination)
        mapView.overlays.add(line)
    }

    private fun cambiarIcono(punto: GeoPoint) {
        val marcador = Marker(mapView)
        marcador.position = punto
        marcador.icon = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))

        mapView.overlays.add(marcador)
    }
    private fun cambioTamañoIcono(icono: Drawable): Drawable {
        val bitmap = (icono as BitmapDrawable).bitmap
        val bitmapCambiado = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
        return BitmapDrawable(resources, bitmapCambiado)
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
