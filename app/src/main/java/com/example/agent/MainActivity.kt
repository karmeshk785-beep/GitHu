package com.example.agent

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val voiceRequest = 101
    private val handler = Handler(Looper.getMainLooper())
    private var selectedId: String? = null

    private lateinit var office: OfficeView
    private lateinit var targetLabel: TextView
    private lateinit var chatLog: TextView
    private lateinit var cmdInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F4F6FA"))
            setPadding(24, 60, 24, 24)
        }

        root.addView(TextView(this).apply {
            text = "Agent Office"
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
        })

        // Upar: animated office
        office = OfficeView(this)
        root.addView(office, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.2f))

        // Agent chunne ke buttons
        val pickRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        AgentRegistry.agents.forEach { agent ->
            pickRow.addView(Button(this).apply {
                text = agent.name
                setOnClickListener { select(agent.id) }
            })
        }
        val pickScroll = HorizontalScrollView(this)
        pickScroll.addView(pickRow)
        root.addView(pickScroll)

        // Neeche: chat aur command
        targetLabel = TextView(this).apply {
            text = "Agent chuno upar se"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.DKGRAY)
            setPadding(0, 16, 0, 8)
        }
        root.addView(targetLabel)

        val chatScroll = ScrollView(this)
        chatLog = TextView(this).apply {
            text = "Command likho ya mic dabakar bolo."
            textSize = 15f
            setTextColor(Color.DKGRAY)
        }
        chatScroll.addView(chatLog)
        root.addView(chatScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        cmdInput = EditText(this).apply { hint = "Yahan likho..." }
        val micButton = Button(this).apply {
            text = "MIC"
            setOnClickListener { startVoice() }
        }
        val sendButton = Button(this).apply {
            text = "BHEJO"
            setOnClickListener { sendCommand() }
        }
        row.addView(cmdInput, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(micButton)
        row.addView(sendButton)
        root.addView(row)

        setContentView(root)
    }

    private fun select(id: String) {
        selectedId = id
        val agent = AgentRegistry.agents.find { it.id == id }
        targetLabel.text = "Agent: ${agent?.name ?: ""}"
    }

    private fun sendCommand() {
        val command = cmdInput.text.toString().trim()
        if (command.isEmpty()) return

        val agent = AgentRegistry.agents.find { it.id == selectedId }
        if (agent == null) {
            chatLog.append("\n\nPehle koi agent chuno.")
            return
        }

        chatLog.append("\n\nAap (${agent.name}): $command")
        cmdInput.setText("")

        agent.status = Status.WORKING
        handler.postDelayed({
            agent.status = Status.IDLE
            chatLog.append("\nAgent: Asli kaam abhi jud nahi hua hai. Ye sirf dikhava hai.")
        }, 1500)
    }

    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Command bolo")
        }
        startActivityForResult(intent, voiceRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceRequest && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            cmdInput.setText(results?.firstOrNull() ?: "")
        }
    }
}
