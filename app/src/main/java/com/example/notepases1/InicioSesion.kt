package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class InicioSesion : AppCompatActivity() {

    companion object{
        var datosUsuario: JSONObject? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        val usuario = findViewById<EditText>(R.id.usuario)
        val contraseña = findViewById<EditText>(R.id.contraseña)
        val ingreso = findViewById<Button>(R.id.buttonIngresar)
        val registro = findViewById<TextView>(R.id.textoLinkRegistro)

        ingreso.setOnClickListener{
            if(validarCuenta(usuario, contraseña)){
                val intentMenu = Intent(this, Menu::class.java)
                startActivity(intentMenu)
            }
        }

        registro.setOnClickListener{
            val intentRegistro = Intent(this,Registro::class.java)
            startActivity(intentRegistro)
        }
    }

    fun validarCuenta (usuario:EditText, contraseña:EditText): Boolean{
        val usuarioDato = usuario.getText().toString().trim()
        val contraseñaDato = contraseña.getText().toString().trim()

        val datosJSON = JSONObject(loadJSONFromAsset())
        val datosUsuarios = datosJSON.getJSONArray("usuarios")

        for(i in 0 until datosUsuarios.length()){
            val dato = datosUsuarios.getJSONObject(i)
            if(dato.getString("usuario") == usuarioDato && dato.getString("contraseña")==contraseñaDato){
                datosUsuario = dato
                return true
            }else if((dato.getString("usuario") == usuarioDato && dato.getString("contraseña")!=contraseñaDato)
                || dato.getString("usuario") != usuarioDato && dato.getString("contraseña")==contraseñaDato){
                Toast.makeText(this, "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        Toast.makeText(this, "No está registrado", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun loadJSONFromAsset(): String? {
        var json: String? = null
        try{
            val istream: InputStream = assets.open( "usuarios.json")
            val size: Int = istream.available()
            val buffer = ByteArray(size)
            istream.read(buffer)
            istream.close()
            json = String(buffer, Charsets. UTF_8)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}