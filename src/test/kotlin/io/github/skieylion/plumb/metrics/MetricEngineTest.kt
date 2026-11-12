package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.metrics.dsl.type
import io.github.skieylion.plumb.model.DecisionKind.IF
import io.github.skieylion.plumb.model.DecisionKind.LOOP
import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MetricEngineTest {

    private class DecisionCount : FunctionMetric {
        override val id = "decisions"

        override fun compute(function: FunctionNode): Double = function.decisionPoints.size.toDouble()
    }

    @Test
    @DisplayName("Every function is measured by every metric in source and registration order")
    fun shouldMeasureEachFunctionWithEachMetricWhenTypeHasSeveralFunctions() {
        val engine = MetricEngine(listOf(CyclomaticComplexity, DecisionCount()))
        val order = type("shop.Order") {
            function("total") { decision(LOOP) }
            function("isEmpty")
        }

        val measurements = engine.measure(order)

        assertThat(measurements).containsExactly(
            Measurement(SymbolId("shop.Order.total", "()"), "cc", 2.0),
            Measurement(SymbolId("shop.Order.total", "()"), "decisions", 1.0),
            Measurement(SymbolId("shop.Order.isEmpty", "()"), "cc", 1.0),
            Measurement(SymbolId("shop.Order.isEmpty", "()"), "decisions", 0.0),
        )
    }

    @Test
    @DisplayName("Functions of nested types are measured after the outer type's own functions")
    fun shouldMeasureNestedTypeFunctionsWhenTypeHasNestedTypes() {
        val engine = MetricEngine(listOf(CyclomaticComplexity))
        val outer = type("shop.Outer") {
            nestedType("Inner") {
                function("clamp", "(int)") { decision(IF, count = 2) }
            }
            function("run")
        }

        val measurements = engine.measure(outer)

        assertThat(measurements).containsExactly(
            Measurement(SymbolId("shop.Outer.run", "()"), "cc", 1.0),
            Measurement(SymbolId("shop.Outer.Inner.clamp", "(int)"), "cc", 3.0),
        )
    }

    @Test
    @DisplayName("A metric that does not apply to a function yields no measurement")
    fun shouldSkipMeasurementWhenMetricDoesNotApply() {
        val engine = MetricEngine(listOf(CyclomaticComplexity))
        val shape = type("shop.Shape") {
            abstractFunction("area")
            function("size") { decision(IF) }
        }

        val measurements = engine.measure(shape)

        assertThat(measurements).containsExactly(Measurement(SymbolId("shop.Shape.size", "()"), "cc", 2.0))
    }

    @Test
    @DisplayName("A type without functions yields no measurements")
    fun shouldReturnEmptyListWhenTypeHasNoFunctions() {
        val engine = MetricEngine(listOf(CyclomaticComplexity))

        val measurements = engine.measure(type("shop.Marker"))

        assertThat(measurements).isEmpty()
    }

    @Test
    @DisplayName("A single function is measured by every applicable metric in registration order")
    fun shouldMeasureFunctionWithEachMetricWhenMeasuredAlone() {
        val engine = MetricEngine(listOf(CyclomaticComplexity, DecisionCount()))
        val total = type("shop.Order") { function("total") { decision(IF) } }.functions.single()

        val measurements = engine.measure(total)

        assertThat(measurements).containsExactly(
            Measurement(SymbolId("shop.Order.total", "()"), "cc", 2.0),
            Measurement(SymbolId("shop.Order.total", "()"), "decisions", 1.0),
        )
    }

    @Test
    @DisplayName("A function is measured by a single metric chosen by its id")
    fun shouldMeasureOnlyChosenMetricWhenMetricIdIsGiven() {
        val engine = MetricEngine(listOf(CyclomaticComplexity, DecisionCount()))
        val total = type("shop.Order") { function("total") { decision(IF) } }.functions.single()

        val measurement = engine.measure(total, "decisions")

        assertThat(measurement).isEqualTo(Measurement(SymbolId("shop.Order.total", "()"), "decisions", 1.0))
    }

    @Test
    @DisplayName("A chosen metric that does not apply to a function yields no measurement")
    fun shouldReturnNullWhenChosenMetricDoesNotApply() {
        val engine = MetricEngine(listOf(CyclomaticComplexity))
        val area = type("shop.Shape") { abstractFunction("area") }.functions.single()

        val measurement = engine.measure(area, "cc")

        assertThat(measurement).isNull()
    }

    @Test
    @DisplayName("Choosing an unknown metric id is rejected with the known ids")
    fun shouldThrowWhenChosenMetricIsUnknown() {
        val engine = MetricEngine(listOf(CyclomaticComplexity, DecisionCount()))
        val total = type("shop.Order") { function("total") }.functions.single()

        assertThatIllegalArgumentException()
            .isThrownBy { engine.measure(total, "cbo") }
            .withMessage("Unknown metric 'cbo', known: [cc, decisions]")
    }

    @Test
    @DisplayName("Requiring an unknown metric lists the known ids in registration order")
    fun shouldThrowWithKnownIdsInRegistrationOrderWhenRequiredMetricIsUnknown() {
        val engine = MetricEngine(listOf(DecisionCount(), CyclomaticComplexity))

        assertThatIllegalArgumentException()
            .isThrownBy { engine.requireKnown("loc") }
            .withMessage("Unknown metric 'loc', known: [decisions, cc]")
    }

    @Test
    @DisplayName("Requiring a known metric passes")
    fun shouldPassWhenRequiredMetricIsKnown() {
        val engine = MetricEngine(listOf(CyclomaticComplexity))

        assertThatCode { engine.requireKnown("cc") }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Metric ids must be unique within an engine")
    fun shouldThrowWhenMetricIdsRepeat() {
        assertThatIllegalArgumentException()
            .isThrownBy { MetricEngine(listOf(CyclomaticComplexity, CyclomaticComplexity)) }
            .withMessageContaining("repeated: [cc]")
    }
}
