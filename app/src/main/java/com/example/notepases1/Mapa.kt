package com.example.notepases1

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.notepases1.databinding.ActivityMapaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
    private lateinit var database: DatabaseReference

    private val startPoint = GeoPoint(4.628593, -74.065041)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference

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

        setContentView(R.layout.activity_mapa)
        bindingMapa = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(bindingMapa.root)

        bindingMapa.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        bindingMapa.osmMap.setMultiTouchControls(true)

        val paraderoId = intent.getIntExtra("id", -1)
        if (paraderoId != -1) {
            mostrarParadero(paraderoId)
            mostrarBusesCercanos(paraderoId)
        } else {
            Toast.makeText(this, "No paradero ID provided", Toast.LENGTH_SHORT).show()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menudesp, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogOut -> {
                FirebaseAuth.getInstance().signOut()
                val intentLogOut = Intent(this, InicioSesion::class.java)
                intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentLogOut)
                finishAffinity()
                true
            }
            R.id.menu -> {
                FirebaseAuth.getInstance().signOut()
                if (InicioSesion.datosUsuario!!.tipo == "conductor") {
                    val intentLogOut = Intent(this, com.example.notepases1.MenuConductor::class.java)
                    intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentLogOut)
                    finishAffinity()
                } else if (InicioSesion.datosUsuario!!.tipo == "pasajero") {
                    val intentLogOut = Intent(this, com.example.notepases1.Menu::class.java)
                    intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentLogOut)
                    finishAffinity()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarParadero(paraderoId: Int) {
        database.child("paraderos").child(paraderoId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitud = snapshot.child("coordenada_x").getValue(Double::class.java) ?: 0.0
                    val longitud = snapshot.child("coordenada_y").getValue(Double::class.java) ?: 0.0
                    val paraderoLocation = GeoPoint(latitud, longitud)
                    agregarMarcadorParadero(paraderoLocation)
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
    }

    private fun mostrarBusesCercanos(paraderoId: Int) {
        val paraderoPath = "paraderos/$paraderoId"
        database.child(paraderoPath).child("buses_id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (busSnapshot in snapshot.children) {
                        val busName = busSnapshot.key ?: continue
                        obtenerUbicacionBus(busName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
    }

    private fun obtenerUbicacionBus(busName: String) {
        database.child("buses").child(busName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val busLocationSnapshot = snapshot.child("location")
                    val lat = busLocationSnapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val lon = busLocationSnapshot.child("lon").getValue(Double::class.java) ?: 0.0
                    val busLocation = GeoPoint(lat, lon)
                    agregarMarcadorBus(busName, busLocation)
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
    }

    private fun agregarMarcadorBus(busName: String, location: GeoPoint) {
        val marcador = Marker(bindingMapa.osmMap)
        marcador.icon = cambioTamañoIcono(resources.getDrawable(R.drawable.iconobus))
        marcador.position = location
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = busName
        bindingMapa.osmMap.overlays.add(marcador)
        marcador.setOnMarkerClickListener { _, _ ->
            cambiarActividad(busName)
            true
        }
    }

    private fun agregarMarcadorParadero(location: GeoPoint) {
        val marcador = Marker(bindingMapa.osmMap)
        marcador.icon = cambioTamañoIcono(resources.getDrawable(R.drawable.punto_rojo))
        marcador.position = location
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.title = "Paradero"
        bindingMapa.osmMap.overlays.add(marcador)
    }

    private fun cambiarActividad(busName: String) {
        val intentBus = Intent(this, InformacionBus::class.java)
        val bundleBus = Bundle()
        bundleBus.putString("nombreBus", busName)
        intentBus.putExtras(bundleBus)
        startActivity(intentBus)
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
    }

    override fun onPause() {
        super.onPause()
        bindingMapa.osmMap.onPause()
    }
}