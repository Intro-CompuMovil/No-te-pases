package com.example.notepases1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class VerParaderos : AppCompatActivity() {

    var arregloParadas: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_paraderos)

        val listaParadas = findViewById<ListView>(R.id.listaParadas)
        val bus = findViewById<TextView>(R.id.bus)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arregloParadas)
        listaParadas.adapter = adapter

        val bundle = Bundle()
        val intentAlerta = Intent(this, ProgramarAlerta::class.java)

        //val bundle: Bundle? = intent.extras
        //val busSeleccionado = bundle?.getString("bus")

        //if (busSeleccionado != null) {
            bus.setText("D81")
            leerParadas("D81",listaParadas)

        //}

        listaParadas.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val paradaSeleccionada = arregloParadas[position]
                llenarBundle(bundle, paradaSeleccionada)
                intentAlerta.putExtras(bundle)
                startActivity(intentAlerta)
            }
        })
    }

    private fun leerParadas(busSeleccionado: String, listaParadas:ListView) {
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
                        //Log.i("paradas",parada.toString())
                    }
                    break
                }
            }
            (listaParadas.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun llenarBundle(bundle: Bundle, itemSelecc: String) {
        bundle.putString("paradaSel", itemSelecc)
    }

}
