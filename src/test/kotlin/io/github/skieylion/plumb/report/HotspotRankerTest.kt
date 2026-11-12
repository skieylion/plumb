package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.StandardMetrics
import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.metrics.dsl.type
import io.github.skieylion.plumb.model.DecisionKind.IF
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HotspotRankerTest {

    private val ranker = HotspotRanker(MetricEngine(StandardMetrics.ALL), Thresholds(mapOf("cc" to 3.0)))

    private val order = type("shop.Order") {
        function("total") { decision(IF, count = 1) }
        function("calculate", "(Order)") { decision(IF, count = 5) }
        nestedType("Line") { function("clamp", "(int)") { decision(IF, count = 3) } }
    }
    private val shape = type("shop.Shape") {
        abstractFunction("area")
        function("size") { decision(IF, count = 2) }
    }

    @Test
    @DisplayName("The highest values come first and only the top ones are kept")
    fun shouldKeepTopFunctionsByDescendingValueWhenLimitIsBelowMeasuredCount() {
        val report = ranker.rank(listOf(order, shape), "cc", limit = 2)

        assertThat(report.hotspots.map { it.verdict.symbol.fqn to it.verdict.value })
            .containsExactly("shop.Order.calculate" to 6.0, "shop.Order.Line.clamp" to 4.0)
    }

    @Test
    @DisplayName("Totals cover every measured function, not only the kept ones")
    fun shouldCountAllMeasuredAndExceedingFunctionsWhenLimitIsBelowMeasuredCount() {
        val report = ranker.rank(listOf(order, shape), "cc", limit = 1)

        assertThat(report)
            .extracting(
                HotspotReport::metricId,
                HotspotReport::threshold,
                HotspotReport::measured,
                HotspotReport::exceeding
            )
            .containsExactly("cc", 3.0, 4, 2)
    }

    @Test
    @DisplayName("Each hotspot carries the verdict against the threshold")
    fun shouldJudgeEachHotspotWhenThresholdIsSet() {
        val report = ranker.rank(listOf(order, shape), "cc", limit = 10)

        assertThat(report.hotspots.map { it.verdict.status })
            .containsExactly(Status.WARN, Status.WARN, Status.OK, Status.OK)
    }

    @Test
    @DisplayName("Equal values are ordered by name, then by signature")
    fun shouldOrderByNameThenSignatureWhenValuesAreEqual() {
        val overloads = type("shop.Order") {
            function("place", "(Order,int)")
            function("place", "(Order)")
            function("cancel", "(Order)")
        }

        val report = ranker.rank(listOf(overloads), "cc", limit = 10)

        assertThat(report.hotspots.map { it.verdict.symbol.fqn + it.verdict.symbol.signature })
            .containsExactly("shop.Order.cancel(Order)", "shop.Order.place(Order)", "shop.Order.place(Order,int)")
    }

    @Test
    @DisplayName("A metric without a threshold reports no threshold and nothing exceeding")
    fun shouldReportNoThresholdWhenMetricHasNoLimit() {
        val report = ranker.rank(listOf(order), "loc", limit = 10)

        assertThat(report)
            .extracting(HotspotReport::threshold, HotspotReport::exceeding)
            .containsExactly(null, 0)
    }

    @Test
    @DisplayName("Ranking by an unknown metric is rejected with the known ids")
    fun shouldThrowWhenMetricIsUnknown() {
        assertThatIllegalArgumentException()
            .isThrownBy { ranker.rank(listOf(order), "cbo", limit = 10) }
            .withMessage("Unknown metric 'cbo', known: [cc, loc]")
    }

    @Test
    @DisplayName("A limit below one is rejected")
    fun shouldThrowWhenLimitIsNotPositive() {
        assertThatIllegalArgumentException()
            .isThrownBy { ranker.rank(listOf(order), "cc", limit = 0) }
            .withMessage("limit must be positive: 0")
    }
}
