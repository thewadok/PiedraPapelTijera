package com.kotliners.piedrapapeltijera.utils.locale

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.kotliners.piedrapapeltijera.MainActivity
import java.util.*
import androidx.core.content.edit

object LocaleManager {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale.Builder()
            .setLanguage(languageCode)
            .build()
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val updatedContext = context.createConfigurationContext(config)
        saveLanguagePreference(context, languageCode)
        return updatedContext
    }

    fun applySavedLocale(context: Context): Context {
        val lang = getSavedLanguage(context)
        return setLocale(context, lang)
    }

    fun updateActivityLocale(activity: Activity, languageCode: String) {
        saveLanguagePreference(activity, languageCode)
        restartApp(activity)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit {
            putString("language", languageCode)
        }
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs.getString("language", Locale.getDefault().language) ?: "es"
    }

    private fun restartApp(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }
}