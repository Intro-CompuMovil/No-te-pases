package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class ProgramarAlerta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar_alerta)

        val boundle = intent.extras
        val  paraderoSeleccionado = findViewById<TextView>(R.id.textParaderoSeleccionado)
        val paraderoAlarma = findViewById<TextView>(R.id.paraderoAlarma)

        //el paradero seleccionado se recibe desde el bus seleccionado en el mapa
        paraderoAlarma.setText(boundle?.getString("paradaSel"))

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
            R.id.menu -> {
                FirebaseAuth.getInstance().signOut()
                if(InicioSesion.datosUsuario!!.tipo == "conductor")
                {
                    val intentLogOut = Intent(this, com.example.notepases1.MenuConductor::class.java)
                    intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentLogOut)
                    finishAffinity()
                }
                else if(InicioSesion.datosUsuario!!.tipo == "pasajero")
                {
                    val intentLogOut = Intent(this, com.example.notepases1.Menu::class.java)
                    intentLogOut.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentLogOut)
                    finishAffinity()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}