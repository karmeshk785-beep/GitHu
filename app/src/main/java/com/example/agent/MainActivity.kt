package com.example.agent

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val voiceRequest = 101
    private val handler = Handler(Looper.getMainLooper())
    private var selected: Agent? = null
    private val cards = mutableMapOf<String, TextView>()

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

        val officeScroll = ScrollView(this)
        val officeGrid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        officeScroll.addView(officeGrid)
        root.addView(officeScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.2f))

        AgentRegistry.agents.chunked(3).forEach { rowAgents ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowAgents.forEach { agent ->
                val card = makeCard(agent)
                row.addView(card, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { setMargins(8, 8, 8, 8) })
            }
            officeGrid.addView(row)
        }

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

    private fun makeCard(agent: Agent): TextView {
        val card = TextView(this).apply {
            text = render(agent)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(12, 24, 12, 24)
            setBackgroundColor(Color.WHITE)
            setOnClickListener { select(agent) }
        }
        cards[agent.id] = card
        return card
    }

    private fun render(agent: Agent): String {
        val icon = when (agent.status) {
            Status.IDLE -> "🤖"
            Status.WORKING -> "⚙️"
            Status.DONE -> "✅"
            Status.FAILED -> "❌"
        }
        return "$icon\n${agent.name}\n${agent.role}"
    }

    private fun setStatus(agent: Agent, status: Status) {
        agent.status = status
        cards[agent.id]?.text = render(agent)
    }

    private fun select(agent: Agent) {
        selected = agent
        targetLabel.text = "Agent: ${agent.name}"
    }

    private fun sendCommand() {
        val command = cmdInput.text.toString().trim()
        val agent = selected
        if (command.isEmpty()) return
        if (agent == null) {
            chatLog.append("\n\nPehle koi agent chuno.")
            return
        }

        chatLog.append("\n\nAap (${agent.name}): $command")
        cmdInput.setText("")
        setStatus(agent, Status.WORKING)

        handler.postDelayed({
            val passed = true
            if (passed) {
                setStatus(agent, Status.DONE)
                chatLog.append("\nReferee: pass, kaam ho gaya.")
            } else {
                setStatus(agent, Status.FAILED)
                chatLog.append("\nReferee: fail, dobara koshish karunga.")
            }
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
}package com.example.agent

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
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val voiceRequest = 101
    private val handler = Handler(Looper.getMainLooper())
    private var selected: Agent? = null
    private val cards = mutableMapOf<String, TextView>()

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

        // Upar: agents ka office (cards)
        val officeScroll = ScrollView(this)
        val officeGrid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        officeScroll.addView(officeGrid)
        root.addView(officeScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.2f))

        AgentRegistry.agents.chunked(3).forEach { rowAgents ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowAgents.forEach { agent ->
                val card = makeCard(agent)
                row.addView(card, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { setMargins(8, 8, 8, 8) })
            }
            officeGrid.addView(row)
        }

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

    private fun makeCard(agent: Agent): TextView {
        val card = TextView(this).apply {
            text = render(agent)
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setPadding(12, 24, 12, 24)
            setBackgroundColor(Color.WHITE)
            setOnClickListener { select(agent) }
        }
        cards[agent.id] = card
        return card
    }

    private fun render(agent: Agent): String {
        val icon = when (agent.status) {
            Status.IDLE -> "🤖"
            Status.WORKING -> "⚙️"
            Status.DONE -> "✅"
            Status.FAILED -> "❌"
        }
        return "$icon\n${agent.name}\n${agent.role}"
    }

    private fun setStatus(agent: Agent, status: Status) {
        agent.status = status
        cards[agent.id]?.text = render(agent)
    }

    private fun select(agent: Agent) {
        selected = agent
        targetLabel.text = "Agent: ${agent.name}"
    }

    private fun sendCommand() {
        val command = cmdInput.text.toString().trim()
        val agent = selected
        if (command.isEmpty()) return
        if (agent == null) {
            chatLog.append("\n\nPehle koi agent chuno.")
            return
        }

        chatLog.append("\n\nAap (${agent.name}): $command")
        cmdInput.setText("")
        setStatus(agent, Status.WORKING)

        // Abhi ka placeholder. Asli kaam aur asli referee baad me jodenge.
        handler.postDelayed({
            val passed = true
            if (passed) {
                setStatus(agent, Status.DONE)
                chatLog.append("\nReferee: pass, kaam ho gaya.")
            } else {
                setStatus(agent, Status.FAILED)
                chatLog.append("\nReferee: fail, dobara koshish karunga.")
            }
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
}package com.example.agent

enum class Status { IDLE, WORKING, DONE, FAILED }

data class Agent(
    val id: String,
    val name: String,
    val role: String,
    var status: Status = Status.IDLE
)

object AgentRegistry {
    val agents = listOf(
        Agent("insta", "Insta", "Instagram helper"),
        Agent("yt", "YouTube", "YouTube helper"),
        Agent("code", "Coder", "Code likhna"),
        Agent("git", "Git", "GitHub push aur build"),
        Agent("phone", "Phone", "Settings, WiFi, alarm"),
        Agent("notes", "Notes", "Notes aur reminder"),
        Agent("msg", "Message", "WhatsApp draft"),
        Agent("study", "Study", "Samjhana aur notes"),
        Agent("web", "Web", "Website kaam (allow list)"),
        Agent("design", "Design", "UI aur icon"),
        Agent("money", "Ideas", "Feature aur kamai ke idea"),
        Agent("referee", "Referee", "Kaam sach me hua ya nahi")
    )
}
