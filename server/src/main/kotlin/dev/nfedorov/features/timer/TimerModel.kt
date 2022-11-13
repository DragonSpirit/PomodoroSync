package dev.nfedorov.features.timer

import kotlinx.serialization.Serializable

@Serializable
data class Action(val type: PayloadActions, val payload: String)

@Serializable
data class NewTimerRequest(val duration: Int)
@Serializable
data class CancelTimerRequest(val uuid: String)
@Serializable
data class TimerStatusResponse(val uuid: String, val status: TimerStatus)

enum class PayloadActions {
    NEW_TIMER,
    CANCEL_TIMER,
}

enum class TimerStatus {
    STARTED,
    END,
    CANCELLED
}