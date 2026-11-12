package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.metrics.dsl.FunctionBuilder
import io.github.skieylion.plumb.metrics.dsl.type
import io.github.skieylion.plumb.model.DecisionKind
import io.github.skieylion.plumb.model.DecisionKind.AND
import io.github.skieylion.plumb.model.DecisionKind.CASE
import io.github.skieylion.plumb.model.DecisionKind.CATCH
import io.github.skieylion.plumb.model.DecisionKind.IF
import io.github.skieylion.plumb.model.DecisionKind.LOOP
import io.github.skieylion.plumb.model.DecisionKind.OR
import io.github.skieylion.plumb.model.DecisionKind.TERNARY
import io.github.skieylion.plumb.model.FunctionNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CyclomaticComplexityTest {

    @Test
    @DisplayName("Straight-line function has a single path")
    fun shouldReturnOneWhenFunctionHasNoDecisionPoints() {
        val function = singleFunction { }

        val complexity = CyclomaticComplexity.compute(function)

        assertThat(complexity).isEqualTo(1.0)
    }

    @ParameterizedTest
    @EnumSource(DecisionKind::class)
    @DisplayName("Every decision kind adds one path")
    fun shouldAddOnePathWhenFunctionHasOneDecisionPoint(kind: DecisionKind) {
        val function = singleFunction { decision(kind) }

        val complexity = CyclomaticComplexity.compute(function)

        assertThat(complexity).isEqualTo(2.0)
    }

    @Test
    @DisplayName("Decision points of all kinds are summed")
    fun shouldSumDecisionPointsWhenFunctionMixesKinds() {
        val function = singleFunction {
            decision(IF, count = 9)
            decision(LOOP)
            decision(CASE, count = 3)
            decision(AND, count = 2)
            decision(OR, count = 2)
            decision(CATCH)
            decision(TERNARY)
        }

        val complexity = CyclomaticComplexity.compute(function)

        assertThat(complexity).isEqualTo(20.0)
    }

    @Test
    @DisplayName("Function without a body is not measured")
    fun shouldReturnNullWhenFunctionHasNoBody() {
        val function = type("shop.Shape") { abstractFunction("area") }.functions.single()

        val complexity = CyclomaticComplexity.compute(function)

        assertThat(complexity).isNull()
    }

    private fun singleFunction(block: FunctionBuilder.() -> Unit): FunctionNode =
        type("shop.OrderService") { function("calculate", "(Order)", block) }.functions.single()
}
