package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MenuConductor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_conductor)

        val textNumPasajeros = findViewById<TextView>(R.id.textNumPasajeros)

        textNumPasajeros.setOnClickListener {
            val intentNumPasajeros = Intent(this, RegistroPasajeros::class.java)
            startActivity(intentNumPasajeros)
        }
    }
}