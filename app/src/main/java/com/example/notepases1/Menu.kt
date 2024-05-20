package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Menu : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        auth = Firebase.auth

        val textRecargar = findViewById<TextView>(R.id.textRecargar)
        val textQR = findViewById<TextView>(R.id.textQR)
        val textStops = findViewById<TextView>(R.id.textParadas)
        val menuFalso = findViewById<ImageButton>(R.id.imageMenu)


        menuFalso.setOnClickListener{
            auth.signOut()
            val intentInicio = Intent(this, InicioSesion::class.java)
            startActivity(intentInicio)
        }

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