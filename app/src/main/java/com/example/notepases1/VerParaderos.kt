package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONObject
import org.osmdroid.util.GeoPoint

class VerParaderos : AppCompatActivity() {

    var arregloParadas: MutableList<String> = ArrayList()
    var paradasGeoMap: HashMap<String, GeoPoint> = hashMapOf(
        "K7 #40" to GeoPoint(4.628243178554855, -74.06538219116572),
        "K7 #45" to GeoPoint(4.6323221460755875, -74.06411513313485),
        "K7 #51" to GeoPoint(4.63811449459301, -74.06342813349394),
        "K7 #56" to GeoPoint(4.642889706166167, -74.06218292000156)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_paraderos)

        val listaParadas = findViewById<ListView>(R.id.listaParadas)
        val bus = findViewById<TextView>(R.id.bus)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arregloParadas)
        listaParadas.adapter = adapter

        bus.setText("D81")
        leerParadas("D81", listaParadas)

        listaParadas.setOnItemClickListener { _, _, position, _ ->
            val paradaSeleccionada = arregloParadas[position]
            val geoPointSeleccionado = paradasGeoMap[paradaSeleccionada]

            val intent = Intent(this, RutaParadero::class.java).apply {
                geoPointSeleccionado?.let {
                    putExtra("latitud", it.latitude)
                    putExtra("longitud", it.longitude)
                }
            }
            startActivity(intent)
        }

    }

    private fun leerParadas(busSeleccionado: String, listaParadas: ListView) {
        try {
            val jsonString = assets.open("buses.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val busesArray = jsonObject.getJSONArray("buses")

            for (i in 0 until busesArray.length()) {
                val bus = busesArray.getJSONObject(i)
                if (bus.getString("nombreBus") == busSeleccionado) {
                    val paradasArray = bus.getJSONArray("paradas")
                    for (j in 0 until paradasArray.length()) {
                        val parada = paradasArray.getJSONObject(j)
                        arregloParadas.add(parada.getString("parada"))
                    }
                    break
                }
            }
            (listaParadas.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
