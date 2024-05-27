package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RecargarCuenta : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var refer: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recargar_cuenta)
        auth = Firebase.auth

        val datosUsuario = InicioSesion.datosUsuario

        val saldoActual = findViewById<TextView>(R.id.textSaldo)
        val saldoRecarga = findViewById<EditText>(R.id.saldoRecarga)
        val aumento = findViewById<ImageButton>(R.id.botonIncremento)
        val decremento = findViewById<ImageButton>(R.id.botonDecremento)
        val guardar = findViewById<Button>(R.id.botonGuardarRecarga)

        datosUsuario?.let { usuario -> saldoActual.text = "Saldo actual: ${usuario.saldo ?: 0}" }

        modificarSaldo(saldoRecarga, aumento, decremento)
        saldoRecarga.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                modificarSaldo(saldoRecarga, aumento, decremento)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        guardar.setOnClickListener {
            if(saldoRecarga.text.toString().toIntOrNull() != null){
                guardarDatos(saldoRecarga, saldoActual)
            }else{
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show()
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun modificarSaldo(saldoRecarga: EditText, aumento: ImageButton, decremento: ImageButton){
        val valorSaldoRecarga = saldoRecarga.text.toString()
        if(valorSaldoRecarga.toIntOrNull() != null || valorSaldoRecarga == ""){
            var saldoR = valorSaldoRecarga.toIntOrNull() ?: 0
            aumento.setOnClickListener {
                saldoR += 1000
                saldoRecarga.setText("$saldoR")
            }
            decremento.setOnClickListener {
                if (saldoR - 1000 > 0){
                    saldoR -= 1000
                    saldoRecarga.setText("$saldoR")
                }
            }
        }else{
            Toast.makeText(this, "Solo se aceptan números enteros", Toast.LENGTH_SHORT).show()
        }
    }

    fun guardarDatos(saldoRecarga: EditText,saldoActual: TextView){
        val nuevoSaldo:Int = saldoRecarga.text.toString().toInt() + (InicioSesion.datosUsuario?.saldo ?: 0)
        InicioSesion.datosUsuario?.saldo = nuevoSaldo
        refer = database.getReference(Paths.PATH_USERS + auth.currentUser!!.uid)
        refer.child("saldo").setValue(nuevoSaldo)
        InicioSesion.datosUsuario.let { usuario ->
            if (usuario != null) {
                saldoActual.text = "Saldo actual: ${usuario.saldo ?: 0}"
            }
        }

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