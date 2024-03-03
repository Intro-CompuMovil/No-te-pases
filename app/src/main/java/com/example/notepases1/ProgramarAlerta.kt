package com.example.notepases1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ProgramarAlerta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_alerta)

        val boundle = intent.extras
        val  paraderoSeleccionado = findViewById<TextView>(R.id.textParaderoSeleccionado)
        val paraderoAlarma = findViewById<TextView>(R.id.paraderoAlarma)

        //el paradero seleccionado se recibe desde el bus seleccionado en el mapa
        paraderoAlarma.setText(boundle?.getString("paradaSel"))
    }
}