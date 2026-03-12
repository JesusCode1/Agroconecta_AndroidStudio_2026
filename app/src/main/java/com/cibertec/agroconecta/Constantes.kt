package com.cibertec.agroconecta

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

class Constantes {

    fun  obtenerTiempoD() : Long{
        return System.currentTimeMillis()
    }

    //Función para obtener una fecha
    fun obtenerFecha (tiempo : Long) : String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy",calendar).toString()
    }
}