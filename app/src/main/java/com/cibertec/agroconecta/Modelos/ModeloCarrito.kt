package com.cibertec.agroconecta.Modelos

class ModeloCarrito {

    var idProducto : String = ""

    var nombre : String = ""

    var precio : String = ""

    var stock: String = ""
    var precioTotal : String = ""
    var cantidad : Int = 0


    constructor()
    constructor(
        cantidad: Int,
        precioTotal: String,
        stock: String,
        precio: String,
        nombre: String,
        idProducto: String
    ) {
        this.cantidad = cantidad
        this.precioTotal = precioTotal
        this.stock = stock
        this.precio = precio
        this.nombre = nombre
        this.idProducto = idProducto
    }


}