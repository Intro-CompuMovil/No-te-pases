package com.example.notepases1

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.notepases1.databinding.ActivityInicioSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class InicioSesion : AppCompatActivity() {

    private lateinit var bindingIniSesion: ActivityInicioSesionBinding

    //Firebase
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var refer: DatabaseReference

    companion object{
        var datosUsuario: Usuario? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        bindingIniSesion = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(bindingIniSesion.root)
        auth = Firebase.auth

        val correo = findViewById<EditText>(R.id.correo)
        val contrasena = findViewById<EditText>(R.id.contrasena)
        val ingreso = findViewById<Button>(R.id.buttonIngresar)
        val registro = findViewById<TextView>(R.id.textoLinkRegistro)

        ingreso.setOnClickListener{
            inicioSesion(correo.text.toString(), contrasena.text.toString())
        }

        registro.setOnClickListener{
            val intentRegistro = Intent(this,Registro::class.java)
            startActivity(intentRegistro)
        }
    }

    //Código relacionado a la autenticación
    private fun inicioSesion(email: String, contrasena: String){
        if(validarCampos() && emailValido(email)){
            auth.signInWithEmailAndPassword(email,contrasena)
                .addOnCompleteListener(this){ task ->
                    Log.d(ContentValues.TAG, "inicioCorreoSesion:onComplete:" + task.isSuccessful)
                    if(task.isSuccessful){
                        Log.d(ContentValues.TAG, "inicioCorreoSesion: success")
                        val usuario = auth.currentUser
                        updateUI(usuario)
                    }else{
                        Log.w(ContentValues.TAG, "inicioCorreoSesion: failure", task.exception)
                        Toast.makeText(this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun validarCampos(): Boolean{
        var valid = true
        val email = bindingIniSesion.correo.text.toString()
        if(TextUtils.isEmpty(email)){
            bindingIniSesion.correo.error = "Requerido."
            valid = false
        }else{
            bindingIniSesion.correo.error = null
        }

        val contrasena = bindingIniSesion.contrasena.text.toString()
        if(TextUtils.isEmpty(contrasena)){
            bindingIniSesion.contrasena.error = "Requerido."
            valid = false
        }else{
            bindingIniSesion.contrasena.error = null
        }
        return valid
    }

    private fun emailValido(email: String): Boolean{
        if(!email.contains("@") || !email.contains(".") || email.length < 5){
            return false
        }
        return true
    }

    private fun updateUI(usuarioActual: FirebaseUser?) {
        if(usuarioActual != null){
            adquirirDatosUsuario()
            if (datosUsuario?.tipo == "pasajero") {
                val intentMenu = Intent(this, Menu::class.java)
                startActivity(intentMenu)
            }
            else if (datosUsuario?.tipo == "conductor") {
                val intentMenuConductor = Intent(this, MenuConductor::class.java)
                startActivity(intentMenuConductor)
            }
        }else{
            bindingIniSesion.correo.setText("")
            bindingIniSesion.contrasena.setText("")
        }
    }

    private fun adquirirDatosUsuario(){
        refer = database.getReference(Paths.PATH_USERS)
        refer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myUser = singleSnapshot.getValue(Usuario::class.java)
                    if (myUser?.uid == auth.currentUser?.uid) {
                        datosUsuario = myUser
                    }
                    Log.i(TAG, "Encontró usuario: " + myUser?.nombre)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException())

            }
        })
    }

    //Código relacionado al ciclo de vida
    override fun onStart() {
        super.onStart()
        val usuarioActual = auth.currentUser
        updateUI(usuarioActual)
    }

}