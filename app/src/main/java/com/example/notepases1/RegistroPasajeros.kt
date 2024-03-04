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

        menu.setOnClickListener {
            val intentMenu = Intent(this, Menu:: class.java)
            startActivity(intentMenu)
        }
    }
}