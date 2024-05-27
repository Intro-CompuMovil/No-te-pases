package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menudesp, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogOut -> {
                FirebaseAuth.getInstance().signOut()
                val intentLogOut = Intent(this, InicioSesion::class.java)
                intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentLogOut)
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}