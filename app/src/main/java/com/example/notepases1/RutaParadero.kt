package com.example.notepases1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.notepases1.databinding.ActivityRutaParaderoBinding
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

class RutaParadero : AppCompatActivity() {
    private lateinit var bindingRutaParadero: ActivityRutaParaderoBinding
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var sensorManejador: SensorManager
    private lateinit var sensorLuz: Sensor
    private lateinit var sensorLuzListener: SensorEventListener
    private lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        sensorManejador = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorLuz = sensorManejador.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        sensorLuzListener = createLightSensorListener()
        roadManager = OSRMRoadManager(this, "ANDROID")
        sensorManejador.registerListener(
            sensorLuzListener,
            sensorLuz,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_paradero)
        bindingRutaParadero = ActivityRutaParaderoBinding.inflate(layoutInflater)
        setContentView(bindingRutaParadero.root)
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

    private fun createLightSensorListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (bindingRutaParadero.osmMap != null) {
                    if (event.values[0] < 3000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0])
                        bindingRutaParadero.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0])
                        bindingRutaParadero.osmMap.overlayManager.tilesOverlay.setColorFilter(null)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
    }

    private fun marcadorDestino(point: GeoPoint, titulo: String) {
        val marcador = Marker(mapView)
        marcador.position = point
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = titulo
        mapView.overlays.add(marcador)
    }


    private fun dibujarRuta(start: GeoPoint, destination: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(destination)
        val road = roadManager.getRoad(routePoints)
        Log.i("OSM_RUTA", "Route lenght: ${road.mLength} klm")
        Log.i("OSM_RUTA", "Duración: ${road.mDuration/60} minutos")
        if(bindingRutaParadero.osmMap != null){
            roadOverlay?.let{bindingRutaParadero.osmMap.overlays.remove(it)}
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay?.outlinePaint?.color = resources.getColor(R.color.Subrayado)
            roadOverlay?.outlinePaint?.strokeWidth = 10f
            bindingRutaParadero.osmMap.overlays.add(roadOverlay)
        }
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
