package com.kourosh.ae

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

/** Lightweight animated scene layer behind the Home controls. No network or VPN state is touched. */
class KouroshSceneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val scene = BitmapFactory.decodeResource(resources, R.drawable.kourosh_scene_top)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlay = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(125, 246, 217, 139)
        setShadowLayer(10f * resources.displayMetrics.density, 0f, 0f, Color.argb(120, 212, 166, 74))
    }
    private val sceneRect = RectF()
    private var fadeShader: Shader? = null
    private val particles = Array(42) { PointF(Random.nextFloat(), Random.nextFloat() * 0.78f) }
    private var phase = 0f
    private var running = false
    private val frame = object : Runnable {
        override fun run() {
            if (!running || !isShown) return
            phase += 0.006f
            invalidate()
            postDelayed(this, 33L)
        }
    }

    init {
        isClickable = false
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        running = true
        removeCallbacks(frame)
        post(frame)
    }

    override fun onDetachedFromWindow() {
        running = false
        removeCallbacks(frame)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(5, 5, 5))
        val w = width.toFloat()
        val h = height.toFloat().coerceAtMost(dp(430f))
        if (scene != null && w > 0f && h > 0f) {
            val scale = maxOf(w / scene.width, h / scene.height)
            val drift = sin(phase * 0.7f) * dp(3f)
            val sw = scene.width * scale
            val sh = scene.height * scale
            val left = (w - sw) / 2f + drift
            val top = -dp(16f) + sin(phase * 0.45f) * dp(2f)
            paint.alpha = 190
            sceneRect.set(left, top, left + sw, top + sh)
            canvas.drawBitmap(scene, null, sceneRect, paint)
        }
        // A dark lower fade keeps the scene behind the live cards rather than competing with them.
        overlay.shader = fadeShader
        canvas.drawRect(0f, 0f, w, h, overlay)
        overlay.shader = null
        particles.forEachIndexed { index, p ->
            val x = p.x * w + sin(phase + index) * dp(2f)
            val y = p.y * h + sin(phase * 0.8f + index * 0.7f) * dp(2f)
            val radius = dp(if (index % 5 == 0) 1.25f else 0.65f)
            canvas.drawCircle(x, y, radius, particlePaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fadeShader = LinearGradient(0f, 0f, 0f, h.toFloat(), Color.argb(0, 5, 5, 5), Color.argb(245, 5, 5, 5), Shader.TileMode.CLAMP)
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
