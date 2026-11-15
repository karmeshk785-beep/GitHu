package com.example.agent

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class OfficeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val handler = Handler(Looper.getMainLooper())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val px = HashMap<String, Float>()
    private val py = HashMap<String, Float>()
    private val tx = HashMap<String, Float>()
    private val ty = HashMap<String, Float>()

    private var seats: List<Pair<Float, Float>> = emptyList()
    private var workSpot = Pair(0f, 0f)

    private val ticker = object : Runnable {
        override fun run() {
            moveAgents()
            invalidate()
            handler.postDelayed(this, 40)
        }
    }

    init {
        handler.postDelayed(ticker, 40)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val cols = 4
        val rows = 3
        val list = mutableListOf<Pair<Float, Float>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val x = w * (c + 0.5f) / cols
                val y = h * 0.35f + h * 0.55f * (r + 0.5f) / rows
                list.add(Pair(x, y))
            }
        }
        seats = list
        workSpot = Pair(w * 0.5f, h * 0.2f)

        AgentRegistry.agents.forEachIndexed { i, agent ->
            val seat = seats[i % seats.size]
            px[agent.id] = seat.first
            py[agent.id] = seat.second
            tx[agent.id] = seat.first
            ty[agent.id] = seat.second
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#F4F6FA"))
        drawRoomItems(canvas)
        AgentRegistry.agents.forEach { drawAgent(canvas, it) }
    }

    private fun drawRoomItems(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        // Safed fridge
        paint.color = Color.WHITE
        canvas.drawRect(w * 0.85f, h * 0.05f, w * 0.97f, h * 0.30f, paint)

        // Neeli sink
        paint.color = Color.parseColor("#1E4E9A")
        canvas.drawRect(w * 0.82f, h * 0.80f, w * 0.99f, h * 0.95f, paint)

        // Laal mez
        paint.color = Color.parseColor("#D32F2F")
        for (r in 0 until 3) {
            val y = h * 0.35f + h * 0.55f * (r + 0.5f) / 3f
            canvas.drawRect(w * 0.08f, y - h * 0.03f, w * 0.78f, y + h * 0.03f, paint)
        }

        // Laal kursiyan
        seats.forEach { seat ->
            canvas.drawRect(
                seat.first - w * 0.04f, seat.second + h * 0.05f,
                seat.first + w * 0.04f, seat.second + h * 0.08f, paint
            )
        }
    }

    private fun drawAgent(canvas: Canvas, agent: Agent) {
        val x = px[agent.id] ?: return
        val y = py[agent.id] ?: return
        val size = width * 0.035f

        paint.color = when (agent.status) {
            Status.IDLE -> Color.parseColor("#90A4AE")
            Status.WORKING -> Color.parseColor("#FFB300")
            Status.DONE -> Color.parseColor("#43A047")
            Status.FAILED -> Color.parseColor("#E53935")
        }
        // Sir
        canvas.drawCircle(x, y - size * 1.2f, size * 0.7f, paint)
        // Dhad
        canvas.drawRoundRect(
            RectF(x - size, y - size * 0.4f, x + size, y + size * 1.2f),
            size * 0.4f, size * 0.4f, paint
        )
        // Aankhein
        paint.color = Color.BLACK
        canvas.drawCircle(x - size * 0.3f, y - size * 1.3f, size * 0.12f, paint)
        canvas.drawCircle(x + size * 0.3f, y - size * 1.3f, size * 0.12f, paint)
        // Naam
        paint.textSize = size * 0.9f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(agent.name, x, y + size * 2.2f, paint)
    }

    private fun moveAgents() {
        if (seats.isEmpty()) return
        AgentRegistry.agents.forEach { agent ->
            val id = agent.id
            val x = px[id] ?: return@forEach
            val y = py[id] ?: return@forEach
            val goalX = tx[id] ?: return@forEach
            val goalY = ty[id] ?: return@forEach

            val dx = goalX - x
            val dy = goalY - y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)

            if (dist < 4f) {
                // Pahunch gaye, naya target chuno
                val next = if (agent.status == Status.WORKING) workSpot
                else seats[Random.nextInt(seats.size)]
                tx[id] = next.first + Random.nextFloat() * 20f - 10f
                ty[id] = next.second + Random.nextFloat() * 20f - 10f
            } else {
                val step = 3f
                px[id] = x + dx / dist * step
                py[id] = y + dy / dist * step
            }
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(ticker)
        super.onDetachedFromWindow()
    }
}
