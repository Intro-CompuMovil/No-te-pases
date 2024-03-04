package com.example.notepases1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class InformacionBus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_bus)

        val cantPasajeros = findViewById<TextView>(R.id.numPasajeros)
        val distanciaParadero = findViewById<TextView>(R.id.kmDistancia)

        //Verificar con Santiago cómo me va a pasar la información de los buses
        val informacionBundle = intent.getBundleExtra("infoCategoria")
        val informacionIntent = intent.getStringExtra("info")
        val categoria = informacionBundle?.getString("categoria")

        //calcular la cantidad de pasajeros
        //cantPasajeros.text =

        //asignar la distancia
        //distanciaParadero.text =
    }
}