// SafetyGate.kt
package com.example.agent.safety

enum class Tool {
    OPEN_APP, TAP, TYPE, SCROLL, READ_SCREEN,
    FILE_READ, FILE_WRITE, TERMUX_RUN,
    GITHUB_PUSH, WEB_FETCH, INSTALL_PAGE,
    SEND_MESSAGE, DELETE, PAYMENT, UNINSTALL, FACTORY_RESET
}

// Ye tools kisi bhi agent ko kabhi nahi milenge, rule card kuch bhi bole
val NEVER_ALLOWED = setOf(Tool.PAYMENT, Tool.FACTORY_RESET)

// Ye tools bina user ki haan ke nahi chalenge
val ALWAYS_CONFIRM = setOf(
    Tool.SEND_MESSAGE, Tool.DELETE, Tool.UNINSTALL,
    Tool.GITHUB_PUSH, Tool.INSTALL_PAGE
)

data class AgentRule(
    val name: String,
    val allowedTools: Set<Tool>,
    val extraConfirm: Set<Tool> = emptySet(),
    val maxSteps: Int = 20,
    val allowedDomains: Set<String> = emptySet(),   // WEB_FETCH ke liye
    val allowedAppPackages: Set<String> = emptySet() // TAP/TYPE ke liye
)

data class Action(
    val tool: Tool,
    val payload: String = "",     // command, text, url, file path
    val targetPackage: String? = null
)

sealed class Decision {
    object Allow : Decision()
    data class Confirm(val reason: String) : Decision()
    data class Deny(val reason: String) : Decision()
}

object SafetyGate {

    // Termux me ye commands kabhi nahi chalenge
    private val blockedShell = listOf(
        Regex("""\brm\s+-rf\s+/"""),
        Regex("""\bsu\b"""), Regex("""\bsudo\b"""),
        Regex("""\bmkfs\b"""), Regex("""\bdd\s+if="""),
        Regex("""curl[^|]*\|\s*(sh|bash)"""),
        Regex("""chmod\s+777\s+/""")
    )

    // Chhote prompt injection / jailbreak patterns
    private val injectionHints = listOf(
        "ignore previous instructions", "ignore all rules",
        "disable safety", "turn off confirmation"
    )

    fun check(rule: AgentRule, action: Action, stepIndex: Int): Decision {
        if (stepIndex >= rule.maxSteps)
            return Decision.Deny("Max steps (${rule.maxSteps}) khatam, task ruk gaya")

        if (action.tool in NEVER_ALLOWED)
            return Decision.Deny("${action.tool} kisi bhi agent ko allowed nahi")

        if (action.tool !in rule.allowedTools)
            return Decision.Deny("${rule.name} ko ${action.tool} ki permission nahi")

        if (injectionHints.any { action.payload.contains(it, ignoreCase = true) })
            return Decision.Deny("Suspicious instruction mila, block kiya")

        when (action.tool) {
            Tool.TERMUX_RUN -> if (blockedShell.any { it.containsMatchIn(action.payload) })
                return Decision.Deny("Ye shell command khatarnak hai")
            Tool.WEB_FETCH -> {
                val host = Regex("""https?://([^/]+)""").find(action.payload)?.groupValues?.get(1)
                if (host == null || rule.allowedDomains.none { host.endsWith(it) })
                    return Decision.Deny("Domain allowlist me nahi: $host")
            }
            Tool.TAP, Tool.TYPE -> {
                val pkg = action.targetPackage
                if (rule.allowedAppPackages.isNotEmpty() && pkg !in rule.allowedAppPackages)
                    return Decision.Deny("Ye app ($pkg) is agent ke liye allowed nahi")
            }
            else -> {}
        }

        if (action.tool in ALWAYS_CONFIRM || action.tool in rule.extraConfirm)
            return Decision.Confirm("${action.tool} se pehle aapki haan chahiye")

        return Decision.Allow
    }

    // Log, screen aur output me secrets chhupao
    private val secretPatterns = listOf(
        Regex("""\b\d{13,19}\b"""),                  // card jaisi numbers
        Regex("""(?i)otp[^\d]{0,10}\d{4,8}"""),      // OTP
        Regex("""ghp_[A-Za-z0-9]{20,}"""),           // GitHub token
        Regex("""sk-[A-Za-z0-9]{16,}"""),            // API key
        Regex("""(?i)password\s*[:=]\s*\S+""")
    )

    fun redact(text: String): String =
        secretPatterns.fold(text) { acc, r -> acc.replace(r, "[HIDDEN]") }
}

// Har decision ka record, taaki baad me dekh sako kya hua
object AuditLog {
    private val entries = mutableListOf<String>()

    fun record(agent: String, action: Action, decision: Decision) {
        val line = "${System.currentTimeMillis()} | $agent | ${action.tool} | $decision"
        entries.add(SafetyGate.redact(line))
    }

    fun all(): List<String> = entries.toList()
}
