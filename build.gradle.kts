val instagramAgent = AgentRule(
    name = "Instagram Helper",
    allowedTools = setOf(Tool.OPEN_APP, Tool.TAP, Tool.SCROLL, Tool.READ_SCREEN, Tool.SEND_MESSAGE),
    extraConfirm = setOf(Tool.TYPE),
    maxSteps = 15,
    allowedAppPackages = setOf("com.instagram.android")
)

val webAgent = AgentRule(
    name = "Web Reader",
    allowedTools = setOf(Tool.WEB_FETCH),
    allowedDomains = setOf("wikipedia.org", "developer.android.com")
)
