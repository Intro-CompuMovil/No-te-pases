package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Mapa : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val bus1 = findViewById<ImageView>(R.id.buses1)
        val bus2 = findViewById<ImageView>(R.id.buses2)

        bus1.setOnClickListener {
            val intentBus1 = Intent(this, InformacionBus::class.java)
            val bundleBus1 = Bundle()
            bundleBus1.putString("id", "1")
            bundleBus1.putString("cantidadP", "30")
            intentBus1.putExtra("info", bundleBus1)
            startActivity(intentBus1)
        }


    }
}