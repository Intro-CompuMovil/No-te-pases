package com.example.notepases1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter

class Registro : AppCompatActivity() {

     object Registrados
     {
         var usuariosRegistrados :MutableList<Usuario> = mutableListOf()
     }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val usuario = findViewById<EditText>(R.id.editTextUsername)
        val contrasena = findViewById<EditText>(R.id.editTextPassword)
        val contrasena2 = findViewById<EditText>(R.id.editTextPassword2)
        val registro = findViewById<Button>(R.id.buttonRegistro)
        val ingreso = findViewById<TextView>(R.id.ingresar)

        /*Registrados.usuariosRegistrados = loadJSONFromAsset()
        ingreso.setOnClickListener {
            val intentIngreso = Intent(this,InicioSesion::class.java)
            startActivity(intentIngreso)
        }*/

        registro.setOnClickListener { registrarUsuario(usuario, contrasena, contrasena2) }
    }

    fun registrarUsuario(usuario:EditText, contrasena:EditText, contrasena2:EditText)
    {
        if(validarExisteUsuario(usuario.toString()))
        {
            if(contrasena.toString().equals(contrasena2.toString()))
            {
                Registrados.usuariosRegistrados.add(Usuario(usuario.toString(),contrasena.toString(),"",0))
                saveJSONToAsset(Registrados.usuariosRegistrados.last())
            }
        }
    }

    fun validarExisteUsuario(usuario : String) : Boolean
    {
        for(i in Registrados.usuariosRegistrados)
        {
            if(i.usuario.equals(usuario))
            {
                return false;
            }
        }
        return true
    }


    fun loadJSONFromAsset(): MutableList<Usuario> {
        var json: String? = null
        try {
            val istream: InputStream = assets.open("usuarios.json")
            val size: Int = istream.available()
            val buffer = ByteArray(size)
            istream.read(buffer)
            istream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return mutableListOf()
        }
        val jsonObject = JSONObject(json)
        val jsonArray = jsonObject.getJSONArray("usuarios")
        val usuarios: MutableList<Usuario> = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val usuarioJSONObject = jsonArray.getJSONObject(i)
            val nombre = usuarioJSONObject.getString("nombre")
            val contrasena = usuarioJSONObject.getString("contrasena")
            usuarios.add(Usuario(nombre, contrasena,"",0))
        }
        return usuarios
    }

    fun saveJSONToAsset(usuario: Usuario) {
        val usuarios = loadJSONFromAsset()
        usuarios.add(usuario)
        val jsonArray = JSONArray()
        for (usuario in usuarios) {
            val jsonObject = JSONObject()
            jsonObject.put("usuario", usuario.usuario)
            jsonObject.put("contraseña", usuario.contrasena)
            jsonObject.put("tipo", usuario.tipo)
            jsonObject.put("saldo", usuario.saldo)
            jsonArray.put(jsonObject)
        }
        val jsonObject = JSONObject()
        jsonObject.put("usuarios", jsonArray)

        try {
            val outputStream = openFileOutput("usuarios.json", Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(outputStream)
            writer.write(jsonObject.toString())
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}