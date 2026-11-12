package io.github.skieylion.plumb.metrics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class StatusTest {

    @Test
    @DisplayName("A single warning makes the aggregate a warning")
    fun shouldReturnWarnWhenAnyStatusIsWarn() {
        val status = Status.worstOf(listOf(Status.OK, Status.WARN, Status.OK))

        assertThat(status).isEqualTo(Status.WARN)
    }

    @Test
    @DisplayName("Nothing to aggregate means OK")
    fun shouldReturnOkWhenThereAreNoStatuses() {
        val status = Status.worstOf(emptyList())

        assertThat(status).isEqualTo(Status.OK)
    }
}
