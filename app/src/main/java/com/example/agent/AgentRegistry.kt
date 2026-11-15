package com.example.agent

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
