package com.naranjapina.heat_tourism.utils

import android.content.Context
import com.naranjapina.heat_tourism.R

/**
 * Helper para leer el access token publico de Mapbox desde strings.xml
 * (Bloque B). Si el token no se cambio del placeholder devuelve cadena vacia
 * para que las llamadas a la API fallen de forma controlada.
 */
object MapboxConfig {
    fun accessToken(context: Context): String {
        val raw = context.getString(R.string.mapbox_access_token)
        return if (raw.isBlank() || raw == "PEGAR_AQUI_EL_PUBLIC_TOKEN") "" else raw
    }
}
