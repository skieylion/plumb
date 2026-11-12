package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.metrics.dsl.type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LinesOfCodeTest {

    @Test
    @DisplayName("Lines of code of a function are its lines containing code")
    fun shouldReturnCodeLinesWhenFunctionHasBody() {
        val function =
            type("shop.OrderService") { function("calculate", "(Order)") { codeLines(79) } }.functions.single()

        val linesOfCode = LinesOfCode.compute(function)

        assertThat(linesOfCode).isEqualTo(79.0)
    }

    @Test
    @DisplayName("Function without a body is not measured")
    fun shouldReturnNullWhenFunctionHasNoBody() {
        val function = type("shop.Shape") { abstractFunction("area") }.functions.single()

        val linesOfCode = LinesOfCode.compute(function)

        assertThat(linesOfCode).isNull()
    }
}
