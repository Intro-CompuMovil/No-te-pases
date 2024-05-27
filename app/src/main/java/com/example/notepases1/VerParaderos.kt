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
import org.osmdroid.util.GeoPoint
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class VerParaderos : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var arregloParadas: MutableList<String> = ArrayList()
    private var paradasGeoMap: HashMap<String, GeoPoint> = hashMapOf(
        "K7 #40" to GeoPoint(4.628243178554855, -74.06538219116572),
        "K7 #45" to GeoPoint(4.6323221460755875, -74.06411513313485),
        "K7 #51" to GeoPoint(4.63811449459301, -74.06342813349394),
        "K7 #56" to GeoPoint(4.642889706166167, -74.06218292000156)
    )

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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun leerParadas(busId: Int) {
        database.child(Paths.PATH_BUSES).child("bus_$busId").child("paradas")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arregloParadas.clear()
                    for (paradaSnapshot in snapshot.children) {
                        val parada = paradaSnapshot.child("parada").value as String
                        arregloParadas.add(parada)
                    }
                    (findViewById<ListView>(R.id.listaParadas).adapter as ArrayAdapter<String>).notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                    error.toException().printStackTrace()
                }
            })
    }
}