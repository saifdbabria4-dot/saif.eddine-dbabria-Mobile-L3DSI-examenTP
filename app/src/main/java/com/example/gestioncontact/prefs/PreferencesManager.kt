package com.example.gestioncontact.prefs

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getTextSizeSp(): Float {
        val sizeStr = prefs.getString("taille_police", "16")
        return sizeStr?.toFloatOrNull() ?: 16f
    }

    fun getTextColor(): Int {
        val colorHex = prefs.getString("couleur_preferee", "#000000") ?: "#000000"
        return try {
            Color.parseColor(colorHex)
        } catch (e: Exception) {
            Color.BLACK
        }
    }

    fun isCallFromListEnabled(): Boolean {
        return prefs.getBoolean("appel_depuis_liste", false)
    }
}
