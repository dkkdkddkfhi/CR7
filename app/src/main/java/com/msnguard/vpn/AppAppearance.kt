package com.msnguard.vpn

import android.content.Context
import android.graphics.Color

/**
 * The Orbit palettes — one dark, one light, chosen by the user.
 *
 * History matters here. The colour picker used to offer five palettes plus a
 * "Dynamic" mode that inherited the phone's Material colours, and that was
 * removed on purpose: the approved design is a specific look, and letting the OS
 * repaint it produced washed-out greys and light surfaces the layout was never
 * designed for.
 *
 * This is not that. There are exactly two palettes, both drawn from an approved
 * mock, and nothing about them is derived from the phone's wallpaper:
 *   - [ORBIT]     — the original dark glass + neon console.
 *   - [PORCELAIN] — a light palette: grey canvas, white raised cards.
 *
 * ## Why a light palette is more than swapping ink for canvas
 *
 * The dark look builds depth out of a white specular highlight on the top edge
 * of every surface. On a white card that highlight is invisible, so a naive
 * inversion produces flat white rectangles floating on flat white. Depth on
 * light has to come from the opposite direction: a shadow below the surface, and
 * a card that is *lighter* than the page rather than darker.
 *
 * That inversion lives in [Sculpt.Lighting], carried on the palette, so every
 * surface in the app changes lighting model together instead of each call site
 * guessing. [load] installs it before any drawing happens.
 *
 * ## Text vs graphics accents
 *
 * `#4FE3C1` mint reads at 11:1 on the dark card and 3.4:1 on white — fine for a
 * dial arc, below the 4.5:1 floor for the letters of a label. So every accent
 * has a `…Text` sibling. On [ORBIT] the two are the same colour; on [PORCELAIN]
 * the text sibling is darkened until it clears 4.5:1 on both the card and the
 * canvas. Shapes keep the vivid colour, which is what makes the light theme
 * still look like MSN-GUARD instead of a generic white app.
 */
object AppAppearance {

    /** Which palette the user picked. Persisted in the shared "settings" store. */
    enum class Mode(val key: String, val enLabel: String, val description: String) {
        DARK("dark", "Dark", "The original console — black glass and neon"),
        LIGHT("light", "Light — theme", "Porcelain — grey page, white cards"),
        ;

        /** Localized at call time so a language switch refreshes the label. */
        val label: String get() = Strings.t(enLabel)

        companion object {
            fun from(key: String?): Mode = entries.firstOrNull { it.key == key } ?: DARK
        }
    }

    const val PREF_KEY = "theme_mode"

    data class Palette(
        /** page background — mock `--void` */
        val canvas: Int,
        /** raised card fill */
        val surface: Int,
        /** recessed / secondary card fill */
        val surfaceVariant: Int,
        /** primary text — mock `--ink` */
        val ink: Int,
        /** secondary text — mock `--dim` */
        val muted: Int,
        /** hairline borders — mock `--line` */
        val divider: Int,
        /** the brand accent — mock `--mint` */
        val primary: Int,
        /**
         * Text and glyphs drawn on top of a surface filled with [primary] — the
         * Save/Done/Reset buttons.
         *
         * Dark on both palettes, which is not an accident: [primary] is a mid-to-
         * bright teal in both, so a light label on it never reaches 4.5:1 while a
         * dark one clears it comfortably.
         */
        val primaryContainer: Int,
        /**
         * Background of a row that is currently selected — a protocol option, a
         * split-tunnel app, a picker entry.
         *
         * Split out from [primaryContainer] because the dark palette could use one
         * value for both and the light palette cannot. Dark: `#04070B`, the canvas,
         * so a selected row reads as a recess with a mint border. Light: a pale
         * mint wash, because filling a selected row with near-black ink on a white
         * page would hide the ink-coloured label sitting on it.
         */
        val selectedSurface: Int,
        /** the "tunnel is up" accent — mock `--neon` */
        val connected: Int,
        val connectedContainer: Int,
        /** tertiary text — mock `--faint` */
        val faint: Int,
        /** download accent — mock `--mint` */
        val mint: Int,
        /** upload accent — mock `--violet` */
        val violet: Int,
        /** in-progress / speed accent — mock `--amber` */
        val amber: Int,
        /** failure accent — mock `--danger` */
        val danger: Int,
        /**
         * Accents again, dark enough to be *read as letters* on this palette's
         * surfaces. Identical to the vivid values on a dark palette, where the
         * vivid values already clear 4.5:1.
         */
        val primaryText: Int = primary,
        val connectedText: Int = connected,
        val mintText: Int = mint,
        val violetText: Int = violet,
        val amberText: Int = amber,
        val dangerText: Int = danger,
        /** Failure text on the connection headline. */
        val error: Int = danger,
        /** How sculpted surfaces are lit on this palette. */
        val lighting: Sculpt.Lighting = Sculpt.DARK_LIGHTING,
    )

    val ORBIT = Palette(
        canvas = 0xFF000000.toInt(),
        surface = 0xFF0D1410.toInt(),
        surfaceVariant = 0xFF131F19.toInt(),
        ink = 0xFFF0FFF6.toInt(),
        muted = 0xFF8FE8B4.toInt(),
        divider = 0xFF1C3527.toInt(),
        primary = 0xFF00FF87.toInt(),
        primaryContainer = 0xFF000000.toInt(),
        selectedSurface = 0xFF000000.toInt(),
        connected = 0xFF3CFF9E.toInt(),
        connectedContainer = 0xFF0A3D22.toInt(),
        faint = 0xFF5F8A72.toInt(),
        mint = 0xFF00FF87.toInt(),
        violet = 0xFF4FD1FF.toInt(),
        amber = 0xFFFFD60A.toInt(),
        danger = 0xFFFF2D55.toInt(),
        error = 0xFFFFB4AB.toInt(),
        lighting = Sculpt.DARK_LIGHTING,
    )

    /**
     * Porcelain: `#F4F9F6` page, `#FFFFFF` cards.
     *
     * The page is deliberately NOT white. A white page with white cards has
     * nothing to separate them but a hairline, and the layout leans on card
     * shapes to group things. Grey page + white card gives the cards their own
     * luminance step, and the shadow from [Sculpt.Lighting.elevationDp] does the
     * rest.
     */
    val PORCELAIN = Palette(
        canvas = 0xFFF4F9F6.toInt(),
        surface = 0xFFFFFFFF.toInt(),
        surfaceVariant = 0xFFEFF6F1.toInt(),
        ink = 0xFF08150D.toInt(),
        muted = 0xFF3E5C4A.toInt(),
        divider = 0xFFDCEBE1.toInt(),
        primary = 0xFF00B85C.toInt(),
        primaryContainer = 0xFF08150D.toInt(),
        selectedSurface = 0xFFDFF7E9.toInt(),
        connected = 0xFF17A05E.toInt(),
        connectedContainer = 0xFFDDF3E6.toInt(),
        faint = 0xFF4A6A57.toInt(),
        mint = 0xFF00B85C.toInt(),
        violet = 0xFF0087B0.toInt(),
        amber = 0xFF9C6E00.toInt(),
        danger = 0xFFD6293F.toInt(),
        primaryText = 0xFF00713A.toInt(),
        connectedText = 0xFF0A7340.toInt(),
        mintText = 0xFF00713A.toInt(),
        violetText = 0xFF00647F.toInt(),
        amberText = 0xFF6B4C00.toInt(),
        dangerText = 0xFFB3261E.toInt(),
        error = 0xFFB3261E.toInt(),
        lighting = Sculpt.LIGHT_LIGHTING,
    )

    fun mode(context: Context): Mode = Mode.from(
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString(PREF_KEY, null)
    )

    fun setMode(context: Context, mode: Mode) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_KEY, mode.key)
            .apply()
    }

    fun palette(mode: Mode): Palette = when (mode) {
        Mode.DARK -> ORBIT
        Mode.LIGHT -> PORCELAIN
    }

    /**
     * The palette for this session, and the only place [Sculpt.lighting] is set.
     *
     * Sculpt's drawing helpers are called from ~26 sites that do not hold a
     * palette (a settings row knows its own colours, not the app's lighting
     * model). Installing the lighting here means every one of those calls is
     * correct by construction, because nothing in the app can obtain a palette
     * without going through this function first.
     */
    fun load(context: Context): Palette = palette(mode(context)).also {
        Sculpt.lighting = it.lighting
    }

    /** Callers use this to pick system-bar icon colour and XML theme. */
    fun isNight(context: Context): Boolean = mode(context) == Mode.DARK

    /** Perceived brightness test, used where only a colour is in hand. */
    fun isDark(color: Int): Boolean =
        (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000 < 140
}
