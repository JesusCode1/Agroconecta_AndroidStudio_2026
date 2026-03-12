package com.cibertec.agroconecta.Modelos

class ModeloOrdenProducto {

    var nombre : String = ""

    var precio : String = ""

    var cantidad : Int = 0

    var precioFinal : String = ""

    constructor()
    constructor(precioFinal: String, cantidad: Int, precio: String, nombre: String) {
        this.precioFinal = precioFinal
        this.cantidad = cantidad
        this.precio = precio
        this.nombre = nombre
    }

}