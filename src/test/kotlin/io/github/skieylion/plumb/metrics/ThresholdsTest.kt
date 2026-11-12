package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ThresholdsTest {

    private val calculate = SymbolId("shop.Order.calculate", "()")
    private val thresholds = Thresholds(mapOf("cc" to 10.0))

    @Test
    @DisplayName("A value above the limit is a warning")
    fun shouldWarnWhenValueExceedsLimit() {
        val verdict = thresholds.judge(Measurement(calculate, "cc", 11.0))

        assertThat(verdict).isEqualTo(Verdict(calculate, "cc", 11.0, 10.0, Status.WARN))
    }

    @Test
    @DisplayName("A value equal to the limit is still fine")
    fun shouldBeOkWhenValueEqualsLimit() {
        val verdict = thresholds.judge(Measurement(calculate, "cc", 10.0))

        assertThat(verdict.status).isEqualTo(Status.OK)
    }

    @Test
    @DisplayName("A metric without a limit is fine and reports no threshold")
    fun shouldBeOkWithoutThresholdWhenMetricHasNoLimit() {
        val verdict = thresholds.judge(Measurement(calculate, "loc", 500.0))

        assertThat(verdict).isEqualTo(Verdict(calculate, "loc", 500.0, null, Status.OK))
    }

    @Test
    @DisplayName("Default thresholds limit cyclomatic complexity to 10 and lines of code to 60")
    fun shouldLimitComplexityAndLinesOfCodeWhenUsingDefaults() {
        assertThat(Thresholds.DEFAULT.limits).containsExactly(entry("cc", 10.0), entry("loc", 60.0))
    }

    @Test
    @DisplayName("Negative or non-finite limits are rejected")
    fun shouldThrowWhenLimitIsNegativeOrNotFinite() {
        assertThatIllegalArgumentException()
            .isThrownBy { Thresholds(mapOf("cc" to -1.0, "loc" to Double.NaN, "cbo" to 5.0)) }
            .withMessageContaining("cc=-1.0")
            .withMessageContaining("loc=NaN")
            .withMessageNotContaining("cbo")
    }
}
