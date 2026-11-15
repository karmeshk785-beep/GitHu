val decision = SafetyGate.check(rule, action, step)
AuditLog.record(rule.name, action, decision)
when (decision) {
    is Decision.Allow -> execute(action)
    is Decision.Confirm -> askUser(decision.reason) { yes -> if (yes) execute(action) }
    is Decision.Deny -> reportToUser(decision.reason) // loop band
}
