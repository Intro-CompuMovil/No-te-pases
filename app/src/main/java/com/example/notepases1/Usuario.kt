package com.example.notepases1

data class Usuario (
    var uid: String,
    var nombre: String,
    var tipo: String,
    var saldo : Int
){
    constructor() : this("","", "", 0)
}