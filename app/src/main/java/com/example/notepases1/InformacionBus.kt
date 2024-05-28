package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class InformacionBus : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_bus)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        val cantPasajeros = findViewById<TextView>(R.id.numPasajeros)
        val distanciaParadero = findViewById<TextView>(R.id.kmDistancia)

        val busName = intent.getStringExtra("nombreBus")
        if (busName != null) {
            obtenerCantidadPasajeros(busName, cantPasajeros)
        } else {
            Toast.makeText(this, "No bus name provided", Toast.LENGTH_SHORT).show()
        }

        val numerosKm = 1..5
        val dato2 = numerosKm.random()
        distanciaParadero.text = "${(dato2).toString()} km"

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

    private fun obtenerCantidadPasajeros(busName: String, textView: TextView) {
        database.child("buses").orderByChild("nombre").equalTo(busName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (busSnapshot in snapshot.children) {
                        val cantidadPasajeros = busSnapshot.child("cantidadPasajeros").getValue(Int::class.java) ?: 0
                        textView.text = "$cantidadPasajeros/80"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
    }
}