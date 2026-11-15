package com.example.agent

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = TextView(this)
        text.text = "Phone Agent chal raha hai"
        text.textSize = 20f
        text.setPadding(40, 120, 40, 40)
        setContentView(text)
    }
}
