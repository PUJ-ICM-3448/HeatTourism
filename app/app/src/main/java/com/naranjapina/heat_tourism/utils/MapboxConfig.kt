package com.naranjapina.heat_tourism.utils

import android.content.Context
import com.naranjapina.heat_tourism.R

object MapboxConfig {
    fun accessToken(context: Context): String {
        val raw = context.getString(R.string.mapbox_access_token).trim()
        return if (
            raw.isBlank() ||
            raw == "PEGAR_AQUI_EL_PUBLIC_TOKEN" ||
            !raw.startsWith("pk.")
        ) "" else raw
    }
}
