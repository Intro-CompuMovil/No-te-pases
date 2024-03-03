package com.example.notepases1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter

class Registro : AppCompatActivity() {

    object Registrados {
        var usuariosRegistrados: MutableList<Usuario> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val usuarioEditText = findViewById<EditText>(R.id.editTextUsername)
        val contrasenaEditText = findViewById<EditText>(R.id.editTextPassword)
        val contrasena2EditText = findViewById<EditText>(R.id.editTextPassword2)
        val registroButton = findViewById<Button>(R.id.buttonRegistro)
        val ingresoTextView = findViewById<TextView>(R.id.ingresar)

        registroButton.setOnClickListener {
            registrarUsuario(usuarioEditText, contrasenaEditText, contrasena2EditText)
        }

       ingresoTextView.setOnClickListener {
            val intentIngreso = Intent(this, InicioSesion::class.java)
            startActivity(intentIngreso)
        }
    }

    private fun registrarUsuario(usuario: EditText, contrasena: EditText, contrasena2: EditText) {
        val usuarioStr = usuario.text.toString()
        val contrasenaStr = contrasena.text.toString()
        val contrasena2Str = contrasena2.text.toString()

        if (validarExisteUsuario(usuarioStr)) {
            if (contrasenaStr == contrasena2Str) {
                Registrados.usuariosRegistrados.add(Usuario(usuarioStr, contrasenaStr, "", 0))
                saveJSONToFile(Registrados.usuariosRegistrados.last())
                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                val intentMenu = Intent(this, Menu::class.java)
                startActivity(intentMenu)
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarExisteUsuario(usuario: String): Boolean {
        return Registrados.usuariosRegistrados.none { it.usuario == usuario }
    }

    private fun loadJSONFromAsset(): MutableList<Usuario> {
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
        }

        val usuarios: MutableList<Usuario> = mutableListOf()
        json?.let {
            val jsonObject = JSONObject(json)
            val jsonArray = jsonObject.getJSONArray("usuarios")
            for (i in 0 until jsonArray.length()) {
                val usuarioJSONObject = jsonArray.getJSONObject(i)
                val nombre = usuarioJSONObject.getString("usuario")
                val contrasena = usuarioJSONObject.getString("contraseña")
                val tipo = usuarioJSONObject.getString("tipo")
                val saldo = usuarioJSONObject.getInt("saldo")
                usuarios.add(Usuario(nombre, contrasena, tipo, saldo))
            }
        }
        return usuarios
    }

    private fun saveJSONToFile(usuario: Usuario) {
        val usuarios = loadJSONFromAsset()
        usuarios.add(usuario)

        val jsonArray = JSONArray()
        usuarios.forEach {
            val jsonObject = JSONObject().apply {
                put("usuario", it.usuario)
                put("contraseña", it.contrasena)
                put("tipo", it.tipo)
                put("saldo", it.saldo)
            }
            jsonArray.put(jsonObject)
        }

        val jsonObject = JSONObject().apply { put("usuarios", jsonArray) }

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
