package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.CyclomaticComplexity
import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.metrics.Verdict
import io.github.skieylion.plumb.metrics.dsl.type
import io.github.skieylion.plumb.model.DecisionKind.IF
import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TypeReporterTest {

    private val thresholds = Thresholds(mapOf("cc" to 2.0))
    private val reporter = TypeReporter(MetricEngine(listOf(CyclomaticComplexity)), thresholds)

    @Test
    @DisplayName("Every measured function gets verdicts against the thresholds")
    fun shouldJudgeEachFunctionWhenReportingType() {
        val order = type("shop.Order") {
            function("calculate", "(Order)") { decision(IF, count = 2) }
            function("isEmpty")
        }

        val report = reporter.report(order)

        assertThat(report.functions.map { it.verdicts }).containsExactly(
            listOf(Verdict(SymbolId("shop.Order.calculate", "(Order)"), "cc", 3.0, 2.0, Status.WARN)),
            listOf(Verdict(SymbolId("shop.Order.isEmpty", "()"), "cc", 1.0, 2.0, Status.OK)),
        )
    }

    @Test
    @DisplayName("The report carries the type identity and the thresholds it was judged by")
    fun shouldCarryTypeIdAndThresholdsWhenReportingType() {
        val report = reporter.report(type("shop.Order"))

        assertThat(report.id).isEqualTo(SymbolId("shop.Order"))
        assertThat(report.thresholds).isEqualTo(thresholds)
    }

    @Test
    @DisplayName("Functions of nested types are reported after the outer type's own functions")
    fun shouldReportNestedFunctionsAfterOwnWhenTypeHasNestedTypes() {
        val outer = type("shop.Outer") {
            nestedType("Inner") { function("clamp", "(int)") }
            function("run")
        }

        val report = reporter.report(outer)

        assertThat(report.functions.map { it.id }).containsExactly(
            SymbolId("shop.Outer.run", "()"),
            SymbolId("shop.Outer.Inner.clamp", "(int)"),
        )
    }

    @Test
    @DisplayName("Functions no metric applies to are left out")
    fun shouldOmitFunctionWhenNothingWasMeasured() {
        val shape = type("shop.Shape") {
            abstractFunction("area")
            function("size")
        }

        val report = reporter.report(shape)

        assertThat(report.functions.map { it.id }).containsExactly(SymbolId("shop.Shape.size", "()"))
    }

    @Test
    @DisplayName("A type is a warning when any of its functions is")
    fun shouldWarnTypeWhenAnyFunctionExceedsThreshold() {
        val order = type("shop.Order") {
            function("isEmpty")
            function("calculate") { decision(IF, count = 5) }
        }

        val report = reporter.report(order)

        assertThat(report.status).isEqualTo(Status.WARN)
    }

    @Test
    @DisplayName("A type within all thresholds is OK")
    fun shouldBeOkWhenNoFunctionExceedsThreshold() {
        val order = type("shop.Order") { function("isEmpty") }

        val report = reporter.report(order)

        assertThat(report.status).isEqualTo(Status.OK)
    }
}
