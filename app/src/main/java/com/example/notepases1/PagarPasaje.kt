package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class PagarPasaje : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var refer: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_pasaje)
        auth = Firebase.auth

        var saldoPasajero = findViewById<TextView>(R.id.saldoPasajero)
        val valorPasaje = findViewById<TextView>(R.id.valorPasaje)
        val BTNPagar = findViewById<Button>(R.id.botonPagar)

        saldoPasajero.setText(InicioSesion.datosUsuario?.saldo.toString())
        valorPasaje.setText("3.000")

        BTNPagar.setOnClickListener {
            val saldoActual = InicioSesion.datosUsuario?.saldo
            val valorPasajeInt = 3000
            if (saldoActual != null && valorPasajeInt != null) {
                if (saldoActual >= valorPasajeInt) {
                    val nuevoSaldo = saldoActual - valorPasajeInt
                    saldoPasajero.text = nuevoSaldo.toString()
                    InicioSesion.datosUsuario?.saldo = nuevoSaldo
                    refer = database.getReference(Paths.PATH_USERS + auth.currentUser!!.uid)
                    refer.child("saldo").setValue(nuevoSaldo)
                } else {
                    Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al obtener el saldo o el valor del pasaje", Toast.LENGTH_SHORT).show()
            }
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