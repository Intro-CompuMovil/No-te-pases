package com.example.notepases1

data class Bus (
    var id:String,
    var numPasajeros:Int,
    var capacidadBus:Int,
){
    constructor() : this("",0,20)
}