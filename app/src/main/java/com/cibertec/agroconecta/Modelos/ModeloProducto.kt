package com.cibertec.agroconecta.Modelos

class ModeloProducto {

    var id: String = ""

    var nombre : String = ""
    var descripcion : String = ""

    var categoria : String = ""

    var precio : String = ""

    var stock : String = ""
    var latitud: String = ""
    var longitud: String = ""
    var direccion: String = ""

    var uid: String = ""

    constructor()
    constructor(
        id: String,
        nombre: String,
        descripcion: String,
        categoria: String,
        precio: String,
        stock: String,
        latitud: String,
        longitud: String,
        direccion: String,
        uid: String
    ) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.categoria = categoria
        this.precio = precio
        this.stock = stock
        this.latitud = latitud
        this.longitud = longitud
        this.direccion = direccion
        this.uid = uid
    }


}