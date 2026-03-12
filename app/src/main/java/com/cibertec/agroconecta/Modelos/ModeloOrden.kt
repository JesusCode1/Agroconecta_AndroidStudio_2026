package com.cibertec.agroconecta.Modelos

class ModeloOrden {

    var idOrden : String = ""

    var ordenadoPor : String = ""

    var tiempoOrden : String = ""

    var totalOrden : String = ""

    var estadoOrden : String = ""

    constructor()
    constructor(
        estadoOrden: String,
        totalOrden: String,
        tiempoOrden: String,
        ordenadoPor: String,
        idOrden: String
    ) {
        this.estadoOrden = estadoOrden
        this.totalOrden = totalOrden
        this.tiempoOrden = tiempoOrden
        this.ordenadoPor = ordenadoPor
        this.idOrden = idOrden
    }


}