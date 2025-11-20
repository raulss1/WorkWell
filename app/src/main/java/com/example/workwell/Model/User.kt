package com.example.workwell.Model
import java.util.Calendar

class User(val name: String, val userName: String, val birthday: String, val email: String) {
    fun edad(): Int
    {
        return yearsBetween(toDay(), toDate(birthday));
    }

    private fun toDate(birthday: String): Date {
        val separado = birthday.split("/")
        return Date(separado[0].toInt(), separado[1].toInt(), separado[2].toInt())
    }


    private fun yearsBetween(hoy: Date, cumple: Date): Int
    {
        val edad = hoy.año - cumple.año
        if (hoy.mes > cumple.mes)
            return edad
        if (hoy.mes < cumple.mes)
            return edad - 1
        if (hoy.dia > cumple.dia)
            return edad
        if (hoy.dia < cumple.dia)
            return edad - 1
        return edad
    }

    private fun toDay(): Date
    {
        val hoy = Calendar.getInstance()
        return Date(hoy.get(Calendar.DAY_OF_MONTH),hoy.get(Calendar.MONTH) + 1,hoy.get(Calendar.YEAR));
    }
}

class Date(val dia: Int, val mes: Int, val año: Int){}