package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class RegistroPasajeros : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_pasajeros)

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
        }

        BTNDescontarPasajero.setOnClickListener {
            if(contadorPasajeros>0)
            {
                contadorPasajeros -= 1
                numPasajeros.text = contadorPasajeros.toString()
            }
        }
    }
}