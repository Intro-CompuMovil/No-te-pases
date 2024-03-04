package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val textRecargar = findViewById<TextView>(R.id.textRecargar)
        val textQR = findViewById<TextView>(R.id.textQR)
        val textNumPasajeros = findViewById<TextView>(R.id.textNumPasajeros)

        textRecargar.setOnClickListener{
            val intentRecargar = Intent(this, RecargarCuenta::class.java)
            startActivity(intentRecargar)
        }
        textQR.setOnClickListener {
            val intentQR = Intent(this, EscanearQR::class.java)
            startActivity(intentQR)
        }
        textNumPasajeros.setOnClickListener {
            val intentNumPasajeros = Intent(this, RegistroPasajeros::class.java)
            startActivity(intentNumPasajeros)
        }

    }
}