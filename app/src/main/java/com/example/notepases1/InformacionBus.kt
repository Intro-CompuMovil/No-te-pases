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

        //Intents y Bundles para próximas entregas
        /*val informacionBundle = intent.getBundleExtra("infoBus")
        val categoria = informacionBundle?.getString("cantPersonas")*/

        val numerosPesonas = 1 .. 80
        val dato1 = numerosPesonas.random()
        cantPasajeros.text = "${(dato1).toString()}/80"

        val numerosKm = 1 .. 5
        val dato2 = numerosKm.random()
        distanciaParadero.text = "${(dato2).toString()} km"
    }
}