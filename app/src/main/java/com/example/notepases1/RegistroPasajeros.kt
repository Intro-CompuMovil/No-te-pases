package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegistroPasajeros : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var refer: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_pasajeros)
        auth = Firebase.auth

        val menu = findViewById<ImageButton>(R.id.imageMenu)
        val numPasajeros = findViewById<TextView>(R.id.numPasajeros)
        val BTNContarPasajero = findViewById<ImageButton>(R.id.botonContarPasajero)
        val BTNDescontarPasajero = findViewById<ImageButton>(R.id.botonDescontarPasajero)
        val BTNAceptar = findViewById<Button>(R.id.botonAceptar)
        var contadorPasajeros = 0
        menu.setOnClickListener {
            val intentMenu = Intent(this, Menu:: class.java)
            startActivity(intentMenu)
        }

        BTNContarPasajero.setOnClickListener {
            contadorPasajeros += 1
            numPasajeros.text = contadorPasajeros.toString()
            InicioSesion.datosBus?.numPasajeros = contadorPasajeros
            refer = database.getReference(Paths.PATH_USERS + auth.currentUser!!.uid)
            refer.child("cantidadPasajeros").setValue(contadorPasajeros)
        }

        BTNDescontarPasajero.setOnClickListener {
            if(contadorPasajeros>0)
            {
                contadorPasajeros -= 1
                numPasajeros.text = contadorPasajeros.toString()
                InicioSesion.datosBus?.numPasajeros = contadorPasajeros
                refer = database.getReference(Paths.PATH_USERS + auth.currentUser!!.uid)
                refer.child("cantidadPasajeros").setValue(contadorPasajeros)
            }
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
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
                if(InicioSesion.datosUsuario!!.tipo == "conductor")
                {
                    val intentLogOut = Intent(this, com.example.notepases1.MenuConductor::class.java)
                    intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentLogOut)
                    finishAffinity()
                }
                else if(InicioSesion.datosUsuario!!.tipo == "pasajero")
                {
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
}