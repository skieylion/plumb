package io.github.skieylion.plumb.metrics

enum class Status {
    OK,
    WARN,
    ;

    companion object {
        fun worstOf(statuses: Iterable<Status>): Status = statuses.maxOrNull() ?: OK
    }
}
