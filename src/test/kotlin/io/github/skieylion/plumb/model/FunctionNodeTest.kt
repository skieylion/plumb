package io.github.skieylion.plumb.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FunctionNodeTest {

    private val id = SymbolId("shop.Order.place", "()")
    private val location = SourceLocation("Order.java", 1, 1)

    @Test
    @DisplayName("A function without a body may have no code lines")
    fun shouldAcceptZeroCodeLinesWhenFunctionHasNoBody() {
        val function = FunctionNode(id, location, false, 0, emptyList())

        assertThat(function.codeLines).isZero()
    }

    @Test
    @DisplayName("Should reject a negative number of code lines")
    fun shouldThrowWhenCodeLinesAreNegative() {
        assertThatIllegalArgumentException()
            .isThrownBy { FunctionNode(id, location, true, -1, emptyList()) }
            .withMessageContaining("codeLines must not be negative: -1")
    }
}
