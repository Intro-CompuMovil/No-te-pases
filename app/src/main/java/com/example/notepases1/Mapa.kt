package com.example.notepases1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.example.notepases1.databinding.ActivityMapaBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class Mapa : AppCompatActivity() {

    private lateinit var bindingMapa: ActivityMapaBinding

    private val startPoint = org.osmdroid.util.GeoPoint(4.628593, -74.065041)

    override fun onCreate(savedInstanceState: Bundle?) {
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        bindingMapa = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(bindingMapa.root)

        bindingMapa.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        bindingMapa.osmMap.setMultiTouchControls(true)

        ubicaciones()
    }

    private fun ubicaciones(){
        val puntoInicio = startPoint
        val punto1 = GeoPoint(4.626404, -74.065593)
        val punto2 = GeoPoint(4.622692, -74.066504)
        val punto3 = GeoPoint(4.617333, -74.068570)

        val marcadorInicio = Marker(bindingMapa.osmMap)
        val marcador1 = Marker(bindingMapa.osmMap)
        val marcador2 = Marker(bindingMapa.osmMap)
        val marcador3 = Marker(bindingMapa.osmMap)

        val iconoInicio = cambioTamañoIcono(resources.getDrawable(R.drawable.ubicacion))
        val icono1 = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))
        val icono2 = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))
        val icono3 = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))

        marcadorInicio.icon = iconoInicio
        marcador1.icon = icono1
        marcador2.icon = icono2
        marcador3.icon = icono3

        marcadorInicio.position = puntoInicio
        marcador1.position = punto1
        marcador2.position = punto2
        marcador3.position = punto3

        marcadorInicio.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        bindingMapa.osmMap.overlays.add(marcadorInicio)

        marcador1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        bindingMapa.osmMap.overlays.add(marcador1)

        marcador2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        bindingMapa.osmMap.overlays.add(marcador2)

        marcador3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        bindingMapa.osmMap.overlays.add(marcador3)

        marcador1.setOnMarkerClickListener { _, _ ->
            cambiarActividad(1, 30)
        }

        marcador2.setOnMarkerClickListener { _, _ ->
            cambiarActividad(2, 40)
        }

        marcador3.setOnMarkerClickListener { _ , _ ->
            cambiarActividad(3, 10)
        }
    }

    private fun cambiarActividad(id: Int, cantidadP: Int): Boolean{
        val intentBus = Intent(this, InformacionBus::class.java)
        val bundleBus = Bundle()
        bundleBus.putString("id", id.toString())
        bundleBus.putString("cantidadP", cantidadP.toString())
        intentBus.putExtra("info",bundleBus)
        startActivity(intentBus)
        return true
    }

    private fun cambioTamañoIcono(icono: Drawable): Drawable {
        val bitmap = (icono as BitmapDrawable).bitmap
        val bitmapCambiado = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
        return BitmapDrawable(resources, bitmapCambiado)
    }

    override fun onResume() {
        super.onResume()
        bindingMapa.osmMap.onResume()
        val mapController: IMapController = bindingMapa.osmMap.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
    }
    override fun onPause() {
        super.onPause()
        bindingMapa.osmMap.onPause()
    }

}