package com.example.notepases1

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.example.notepases1.databinding.ActivityMapaBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay

class Mapa : AppCompatActivity() {

    private lateinit var bindingMapa: ActivityMapaBinding
    private lateinit var sensorManejador: SensorManager
    private lateinit var sensorLuz: Sensor
    private lateinit var sensorLuzListener: SensorEventListener

    private val startPoint = org.osmdroid.util.GeoPoint(4.628593, -74.065041)

    override fun onCreate(savedInstanceState: Bundle?) {
        sensorManejador = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorLuz = sensorManejador.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        sensorLuzListener = createLightSensorListener()
        sensorManejador.registerListener(
            sensorLuzListener,
            sensorLuz,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        bindingMapa = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(bindingMapa.root)

        bindingMapa.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        bindingMapa.osmMap.setMultiTouchControls(true)

        ubicaciones(0,4.628593, -74.065041, true)
        ubicaciones(1,4.626404, -74.065593, false)
        ubicaciones(2,4.622692, -74.066504, false)
        ubicaciones(3,4.617333, -74.068570, false)
    }

    private fun ubicaciones(id: Int, latitud: Double, longitud: Double, inicio: Boolean){
        val punto = GeoPoint(latitud, longitud)

        val marcador = Marker(bindingMapa.osmMap)

        if (inicio){
            marcador.icon = cambioTamañoIcono(resources.getDrawable(R.drawable.ubicacion))
        } else {
            marcador.icon = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))
        }

        marcador.position = punto

        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        bindingMapa.osmMap.overlays.add(marcador)

        if(!inicio){
            marcador.setOnMarkerClickListener { _, _ ->
                cambiarActividad(id, 30)
            }
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

    private fun createLightSensorListener(): SensorEventListener {
    return object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (bindingMapa.osmMap != null) {
                if (event.values[0] < 3000) {
                    Log.i("MAPS", "DARK MAP " + event.values[0])
                    bindingMapa.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                } else {
                    Log.i("MAPS", "LIGHT MAP " + event.values[0])
                    bindingMapa.osmMap.overlayManager.tilesOverlay.setColorFilter(null)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}

    override fun onResume() {
        super.onResume()
        bindingMapa.osmMap.onResume()
        val mapController: IMapController = bindingMapa.osmMap.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)

        /*val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            bindingMapa.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        */


    }
    override fun onPause() {
        super.onPause()
        bindingMapa.osmMap.onPause()
    }

}