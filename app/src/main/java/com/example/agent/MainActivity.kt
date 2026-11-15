package com.example.agent

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var chatBox: TextView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            setPadding(24, 60, 24, 24)
        }

        val title = TextView(this).apply {
            text = "Phone Agent"
            textSize = 22f
            setTextColor(Color.BLACK)
        }
        root.addView(title)

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        chatBox = TextView(this).apply {
            text = "Command likho, main jawab dunga."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 24, 0, 24)
        }
        scroll.addView(chatBox)
        root.addView(scroll)

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        input = EditText(this).apply {
            hint = "Yahan likho..."
            layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        }
        val sendButton = Button(this).apply {
            text = "Bhejo"
            setOnClickListener { sendCommand() }
        }
        row.addView(input)
        row.addView(sendButton)
        root.addView(row)

        setContentView(root)
    }

    private fun sendCommand() {
        val command = input.text.toString().trim()
        if (command.isEmpty()) return

        chatBox.append("\n\nAap: $command")
        chatBox.append("\nAgent: Command mil gaya. Asli dimaag agle step me jodenge.")
        input.setText("")
    }
}
