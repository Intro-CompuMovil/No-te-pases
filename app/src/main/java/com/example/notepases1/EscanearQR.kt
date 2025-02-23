package com.example.notepases1

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import androidx.camera.core.ImageCaptureException
import com.example.notepases1.databinding.ActivityEscanearQrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.json.JSONObject
import java.io.FileNotFoundException
import java.util.Locale

class EscanearQR : AppCompatActivity() {

    //Código tomado y modificado del curso de Android Studio
    //https://developer.android.com/codelabs/camerax-getting-started?hl=es-419#0
    private lateinit var vistaBindingQR: ActivityEscanearQrBinding

    private var capturarImagen: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vistaBindingQR = ActivityEscanearQrBinding.inflate(layoutInflater)
        setContentView(vistaBindingQR.root)

        val botonQR = vistaBindingQR.buttonLeerQR

        // Request camera permissions
        if (allPermissionsGranted()) {
            vistaPrevia()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        botonQR.setOnClickListener { tomarFotico() }

        cameraExecutor = Executors.newSingleThreadExecutor()
        setSupportActionBar(vistaBindingQR.toolbar)
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
    private fun tomarFotico() {
        val capturarImagen = capturarImagen ?: return

        val nombreImagen = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val valorImagen = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreImagen)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ProyectoNoTePases")
            }
        }

        val resultadoImagenGaleria = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                valorImagen)
            .build()

        capturarImagen.takePicture(
            resultadoImagenGaleria,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext,"No se pudo tomar la foto: ${exc.message})",Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    Toast.makeText(baseContext, "Captura de pantalla existosa: ${output.savedUri}", Toast.LENGTH_SHORT).show()
                    val contenido = decodeQRImage(output.savedUri!!)
                    if(contenido != null) {
                        val jsonUri = JSONObject(contenido)
                        Log.i("QR", jsonUri.toString())
                        identificadorIntent(jsonUri)
                    }
                }
            }
        )
    }

    private fun decodeQRImage(uri: Uri): String? {
        try{
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val sourceQr: LuminanceSource = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(sourceQr))
            val reader = MultiFormatReader()
            val resultado = reader.decode(binaryBitmap)
            Log.i("CorrecciónQR", resultado.text)
            return resultado.text
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        } catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    private fun identificadorIntent(jsonUri: JSONObject){
        when(jsonUri.getString("objeto")){
            "paradero" -> {
                //Indica que tan lejos están los buses del paradero
                val intentParadero = Intent(this, Mapa::class.java)
                val bundleParadero = Bundle()
                bundleParadero.putInt("id", jsonUri.getInt("id"))
                intentParadero.putExtras(bundleParadero)
                startActivity(intentParadero)
            }
            "pasaje" -> {
                //Pagar el pasaje y descontar el saldo
                val intentPagarPasaje = Intent(this, PagarPasaje::class.java)
                startActivity(intentPagarPasaje)
            }
            "bus" -> {
                //Muestra las próximas paradas del bus
                val intentBus = Intent(this, VerParaderos::class.java)
                val bundleBus = Bundle()
                bundleBus.putInt("id", jsonUri.getInt("id"))
                intentBus.putExtras(bundleBus)
                startActivity(intentBus)
            }
        }
    }

    private fun vistaPrevia() {
        val provedorCamara = ProcessCameraProvider.getInstance(this)

        provedorCamara.addListener({
            val cameraProvider: ProcessCameraProvider = provedorCamara.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(vistaBindingQR.vistaPrevia.surfaceProvider)
                }

            this.capturarImagen = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, capturarImagen)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                vistaPrevia()
            } else {
                Toast.makeText(this,
                    "Permiso no otorgado por el nombre",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
         }.toTypedArray()
    }
}

