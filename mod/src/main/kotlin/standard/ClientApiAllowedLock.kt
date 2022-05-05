package standard

class ClientApiAllowedLock {
    @Volatile
    private var lock = false

    @Suppress("ControlFlowWithEmptyBody")
    fun lock() {
        while (lock) {}
        lock = true
    }

    fun unlock() {
        lock = false
    }
}
