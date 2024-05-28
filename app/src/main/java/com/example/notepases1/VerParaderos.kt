package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.util.GeoPoint

class VerParaderos : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var arregloParadas: MutableList<String> = ArrayList()
    private var paradasGeoMap: HashMap<String, GeoPoint> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_paraderos)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        val listaParadas = findViewById<ListView>(R.id.listaParadas)
        val bus = findViewById<TextView>(R.id.bus)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arregloParadas)
        listaParadas.adapter = adapter

        val busId = intent.getIntExtra("id", -1)
        if (busId != -1) {
            leerParadas(busId)
        } else {
            // Handle error: no bus ID provided
            Toast.makeText(this, "No bus ID provided", Toast.LENGTH_SHORT).show()
        }

        listaParadas.setOnItemClickListener { _, _, position, _ ->
            val paradaSeleccionada = arregloParadas[position]
            val geoPointSeleccionado = paradasGeoMap[paradaSeleccionada]

            val intent = Intent(this, RutaParadero::class.java).apply {
                geoPointSeleccionado?.let {
                    putExtra("latitud", it.latitude)
                    putExtra("longitud", it.longitude)
                }
            }
            startActivity(intent)
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

    private fun leerParadas(busId: Int) {
        database.child(Paths.PATH_BUSES).child("bus_$busId").child("paradas")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arregloParadas.clear()
                    paradasGeoMap.clear()
                    for (paradaSnapshot in snapshot.children) {
                        val paradaId = paradaSnapshot.key ?: continue
                        val para = paradaSnapshot.child("para").getValue(Int::class.java) ?: 0
                        if (para == 1) {
                            obtenerParada(paradaId)
                        }
                    }
                    (findViewById<ListView>(R.id.listaParadas).adapter as ArrayAdapter<String>).notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                    error.toException().printStackTrace()
                }
            })
    }

    private fun obtenerParada(paradaId: String) {
        database.child(Paths.PATH_PARADEROS).child(paradaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombreParada = snapshot.child("nombre").getValue(String::class.java) ?: ""
                    val latitud = snapshot.child("coordenada_x").getValue(Double::class.java) ?: 0.0
                    val longitud = snapshot.child("coordenada_y").getValue(Double::class.java) ?: 0.0
                    val geoPoint = GeoPoint(latitud, longitud)

                    arregloParadas.add(nombreParada)
                    paradasGeoMap[nombreParada] = geoPoint

                    (findViewById<ListView>(R.id.listaParadas).adapter as ArrayAdapter<String>).notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                    error.toException().printStackTrace()
                }
            })
    }
}
