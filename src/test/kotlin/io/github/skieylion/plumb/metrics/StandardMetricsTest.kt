package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.metrics.dsl.type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class StandardMetricsTest {

    @Test
    @DisplayName("Standard metrics measure complexity first, then lines of code")
    fun shouldMeasureComplexityThenLinesOfCodeWhenEngineUsesStandardMetrics() {
        val function =
            type("shop.OrderService") { function("calculate", "(Order)") { codeLines(12) } }.functions.single()

        val measurements = MetricEngine(StandardMetrics.ALL).measure(function)

        assertThat(measurements.map { it.metricId to it.value }).containsExactly("cc" to 1.0, "loc" to 12.0)
    }
}
