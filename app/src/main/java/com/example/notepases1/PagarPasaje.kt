package com.example.notepases1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class PagarPasaje : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_pasaje)

        var saldoPasajero = findViewById<TextView>(R.id.saldoPasajero)
        val valorPasaje = findViewById<TextView>(R.id.valorPasaje)
        val BTNPagar = findViewById<Button>(R.id.botonPagar)

        saldoPasajero.setText(InicioSesion.datosUsuario?.getString("saldo"))
        valorPasaje.setText("3.000")

        /*BTNPagar.setOnClickListener {
            val saldo = InicioSesion.datosUsuario?.getString("saldo")?.toIntOrNull()
            if (saldo != null) {
                val nuevoSaldo = saldo - 3000
                InicioSesion.datosUsuario?.put("saldo", nuevoSaldo.toString())
                saldoPasajero.text = nuevoSaldo.toString()
            }
        }*/
        BTNPagar.setOnClickListener {
            val saldoActual = InicioSesion.datosUsuario?.getString("saldo")?.toIntOrNull()
            val valorPasajeInt = 3000
            if (saldoActual != null && valorPasajeInt != null) {
                if (saldoActual >= valorPasajeInt) {
                    val nuevoSaldo = saldoActual - valorPasajeInt
                    saldoPasajero.text = nuevoSaldo.toString()
                    InicioSesion.datosUsuario?.put("saldo", nuevoSaldo.toString())
                } else {
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al obtener el saldo o el valor del pasaje", Toast.LENGTH_SHORT).show()
            }
        }
    }
}