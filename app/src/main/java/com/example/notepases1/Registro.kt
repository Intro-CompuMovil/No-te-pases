package com.example.notepases1

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.notepases1.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class Registro : AppCompatActivity() {

    //Binding
    private lateinit var bindingRegistro: ActivityRegistroBinding

    //Firebase
    private lateinit var autenticacion: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private val database = FirebaseDatabase.getInstance()
    private lateinit var refer: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        bindingRegistro = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(bindingRegistro.root)
        autenticacion = Firebase.auth
        storage = Firebase.storage

        val correo = findViewById<EditText>(R.id.editTextCorreo)
        val contrasena = findViewById<EditText>(R.id.editTextPassword)
        val registroButton = findViewById<Button>(R.id.buttonRegistro)
        val ingresoTextView = findViewById<TextView>(R.id.ingresar)

        registroButton.setOnClickListener {
            if (validarCampos()){
                verificarCorreoExistente(correo.text.toString()){estadoCorreo ->
                    if (!estadoCorreo){
                        registroUsarioAuthentication(correo.text.toString(), contrasena.text.toString())
                    }
                }
            }
        }

       ingresoTextView.setOnClickListener {
            val intentIngreso = Intent(this, InicioSesion::class.java)
            startActivity(intentIngreso)
        }
    }

    //Validación de campos

    private fun verificarCorreoExistente(correo: String, onComplete: (Boolean) -> Unit) {
        autenticacion.fetchSignInMethodsForEmail(correo)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods?.isEmpty() == true) {
                        Toast.makeText(this, "El correo electrónico está disponible para registro", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    } else {
                        Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    }
                } else {
                    // Error al verificar el correo electrónico
                    Toast.makeText(this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }
    }

    private fun validarCampos(): Boolean{
        var valid = true

        val email = bindingRegistro.editTextCorreo.text.toString()
        if(TextUtils.isEmpty(email)){
            bindingRegistro.editTextCorreo.error = "Requerido."
            valid = false
        }else{
            bindingRegistro.editTextCorreo.error = null
        }

        val contrasena = bindingRegistro.editTextPassword.text.toString()
        if(TextUtils.isEmpty(contrasena)){
            bindingRegistro.editTextPassword.error = "Requerido."
            valid = false
        }else{
            bindingRegistro.editTextPassword.error = null
        }

        val nombre = bindingRegistro.editTextNombre.text.toString()
        if(TextUtils.isEmpty(nombre)){
            bindingRegistro.editTextNombre.error = "Requerido."
            valid = false
        }else{
            bindingRegistro.editTextNombre.error = null
        }

        if (!bindingRegistro.usuarioPasajero.isChecked && !bindingRegistro.usuarioBus.isChecked) {
            Toast.makeText(this, "Seleccione un tipo de usuario", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (bindingRegistro.usuarioPasajero.isChecked && bindingRegistro.usuarioBus.isChecked) {
            Toast.makeText(this, "Debe selccionar solo un tipo de usuario", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    //Código de registro
    private fun registroUsarioAuthentication(email: String, contrasena: String){
        autenticacion.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Log.d(ContentValues.TAG, "crearUsuarioCorreo: onComplete: " + task.isSuccessful)
                    val usuario = autenticacion.currentUser
                    if(usuario != null){
                        val actualizacionUsarios = UserProfileChangeRequest.Builder()
                        actualizacionUsarios.setDisplayName(email)
                        usuario.updateProfile(actualizacionUsarios.build())
                        updateUI(usuario)
                        registrarUsuarioRealtimeDatabase()
                    }
                }else{
                    Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
                    task?.exception?.message?.let { Log.w(ContentValues.TAG, it)}
                }
            }
    }

    private fun updateUI(usuarioActual: FirebaseUser?) {
        if(usuarioActual != null){
            val intentInicioSesion = Intent(this, InicioSesion::class.java)
            startActivity(intentInicioSesion)
        }else{
            bindingRegistro.editTextCorreo.setText("")
            bindingRegistro.editTextPassword.setText("")
            bindingRegistro.editTextNombre.setText("")
        }
    }

    //Código relacionado con el realtime database
    private fun registrarUsuarioRealtimeDatabase(){
        val nombre = bindingRegistro.editTextNombre
        val pasajero = bindingRegistro.usuarioPasajero
        val conductor = bindingRegistro.usuarioBus

        val usuarioRegistro = Usuario()
        usuarioRegistro.uid = autenticacion.currentUser!!.uid
        usuarioRegistro.nombre = nombre.text.toString()
        usuarioRegistro.saldo = 0

        if(pasajero.isChecked){
            usuarioRegistro.tipo = "pasajero"
        }else if (conductor.isChecked){
            usuarioRegistro.tipo = "conductor"
        }

        refer = database.getReference(Paths.PATH_USERS+autenticacion.currentUser!!.uid)
        refer.setValue(usuarioRegistro)
    }



}
