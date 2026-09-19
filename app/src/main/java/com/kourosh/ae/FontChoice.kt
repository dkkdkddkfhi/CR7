package com.kourosh.ae

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

/** Persisted UI font choice. Missing optional files intentionally fall back to system. */
object FontChoice {
    private const val PREF = "ui_font_choice"
    private const val KEY = "family"
    private const val SCALE_KEY = "text_scale"

    enum class Family(val key: String, val label: String, val resource: Int?) {
        SYSTEM("system", "System default", null),
        YEKAN("yekan", "Yekan", null),
        YEKAN_BOLD("yekan_bold", "Yekan Bold", null),
        YEKAN_BOOM("yekan_boom", "Yekan Boom", null),
        DAST_NEVIS("dast_nevis", "Dast Nevis", null),
        IRAN_NASTALIQ("iran_nastaliq", "Iran Nastaliq", null),
        IRANIAN_SANS("iranian_sans", "Iranian Sans", null),
        VAZIRMATN("vazirmatn", "Vazirmatn", R.font.vazirmatn_regular),
        VAZIRMATN_BOLD("vazirmatn_bold", "Vazirmatn Bold", R.font.vazirmatn_bold),
        NOTO_SANS("noto_sans", "Noto Sans", R.font.noto_sc_regular),
        NOTO_SANS_MEDIUM("noto_sans_medium", "Noto Sans Medium", R.font.noto_sc_medium),
        ;

        val available: Boolean get() = resource != null
    }

    fun current(context: Context): Family {
        val key = context.profiled().getString(KEY, Family.SYSTEM.key)
        return Family.entries.firstOrNull { it.key == key } ?: Family.SYSTEM
    }

    fun select(context: Context, family: Family) {
        context.profiled().edit().putString(KEY, family.key).apply()
    }

    fun scale(context: Context): Float = context.profiled().getFloat(SCALE_KEY, 1f).coerceIn(0.9f, 1.3f)

    fun setScale(context: Context, value: Float) {
        context.profiled().edit().putFloat(SCALE_KEY, value.coerceIn(0.9f, 1.3f)).apply()
    }

    fun regular(context: Context): Typeface {
        val chosen = current(context)
        return chosen.resource?.let { runCatching { ResourcesCompat.getFont(context, it) }.getOrNull() }
            ?: Typeface.create("sans", Typeface.NORMAL)
    }

    fun medium(context: Context): Typeface {
        val chosen = current(context)
        return when (chosen) {
            Family.VAZIRMATN, Family.VAZIRMATN_BOLD -> runCatching { ResourcesCompat.getFont(context, R.font.vazirmatn_bold) }.getOrNull()
            Family.NOTO_SANS, Family.NOTO_SANS_MEDIUM -> runCatching { ResourcesCompat.getFont(context, R.font.noto_sc_medium) }.getOrNull()
            else -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
        } ?: Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
}
