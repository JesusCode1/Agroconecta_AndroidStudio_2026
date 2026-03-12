package com.cibertec.agroconecta.Modelos

import android.net.Uri

class ModeloImagenSeleccionada {
    var id = ""

    /*identifica imagen que selecciona a la camara o galeria*/
    var imageUri : Uri?= null
    /*imagen traidas de firebase*/
    var imagenUrl : String ?=null
    /*indica si es v o f de internet*/
    var deInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imagenUrl: String?, deInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imagenUrl = imagenUrl
        this.deInternet = deInternet
    }


}