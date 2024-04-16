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
        val textStops = findViewById<TextView>(R.id.textParadas)


        textRecargar.setOnClickListener{
            val intentRecargar = Intent(this, RecargarCuenta::class.java)
            startActivity(intentRecargar)
        }
        textQR.setOnClickListener {
            val intentQR = Intent(this, EscanearQR::class.java)
            startActivity(intentQR)
        }
        textStops.setOnClickListener {
            val intentStops = Intent(this, Paradas::class.java)
            startActivity(intentStops)
        }
    }
}