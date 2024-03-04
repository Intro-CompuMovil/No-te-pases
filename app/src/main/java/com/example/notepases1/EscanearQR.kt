package com.example.notepases1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.math.log
import androidx.activity.result.contract.ActivityResultContracts

class EscanearQR : AppCompatActivity() {

    /*private val lectorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK){
            val contenido = result.data?.getStringExtra()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanear_qr)

        val botonCamara = findViewById<Button>(R.id.buttonLeerQR)
        Log.i("PAPITAS","papitaaaaaaaaaaaas")

        botonCamara.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    camaraActiva()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.CAMERA
                ) -> {
                    Toast.makeText(
                        this,
                        "Activa cámara para poder usar más funciones en nuestra app",
                        Toast.LENGTH_SHORT
                    )
                }
                else -> {
                    permisoCamara()
                }
            }
        }
    }

    private fun permisoCamara() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            Companion.REQUEST_IMAGE_CAPTURE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Companion.REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    // Puedes iniciar la actividad de la cámara aquí
                    camaraActiva()
                } else {
                    // Permiso denegado
                    // Puedes manejar el caso en el que el usuario deniega el permiso
                }
                return
            }

            else -> {
                // Ignorar todas las demás solicitudes.
            }
        }
    }

    fun camaraActiva(){
        val intCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            //startActivityForResult(intCamara, REQUEST_IMAGE_CAPTURE)
            IntentIntegrator(this).initiateScan()
        }catch (e: ActivityNotFoundException){
            e.message?.let { Log.e("PERMISO_CAMARA",it)}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val imageBitmap = data?.extras?.get("data") as? Bitmap
            val imagen = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (imagen != null){
                if(imagen.contents == null){
                    Log.i("IMAGEN","Vainas raras")
                }else{
                    Toast.makeText(this,"El valor es: "+imagen.contents,Toast.LENGTH_SHORT).show()
                    Log.i("IMAGEN", "Se logro")
                }
            }else{
                super.onActivityResult(requestCode, resultCode, data)
                Log.i("IMAGEN", "Faiamos")
            }
            Log.i("IMAGEN","Re hiper mega morimos")
        }

    }

    fun decodificarQRcode(bitmap: Bitmap?){

    }
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 123
    }
}

