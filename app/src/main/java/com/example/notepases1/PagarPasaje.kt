package com.example.notepases1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.widget.Button
import android.widget.TextView


class PagarPasaje : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_pasaje)

        var saldoPasajero = findViewById<TextView>(R.id.saldoPasajero)
        val valorPasaje = findViewById<TextView>(R.id.valorPasaje)
        val BTNPagar = findViewById<Button>(R.id.botonPagar)

        saldoPasajero.setText(InicioSesion.datosUsuario?.getString("saldo"))
        valorPasaje.setText("3.000")

        BTNPagar.setOnClickListener {
            val saldo = InicioSesion.datosUsuario?.getString("saldo")?.toIntOrNull()
            if (saldo != null) {
                val nuevoSaldo = saldo - 3000
                InicioSesion.datosUsuario?.put("saldo", nuevoSaldo.toString())
                saldoPasajero.text = nuevoSaldo.toString()
            }
        }
    }
}