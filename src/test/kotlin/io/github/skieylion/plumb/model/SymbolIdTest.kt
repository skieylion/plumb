package io.github.skieylion.plumb.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SymbolIdTest {

    @Test
    @DisplayName("Should distinguish overloads by signature")
    fun shouldNotBeEqualWhenSignaturesDiffer() {
        val byInt = SymbolId("shop.Order.total", "(int)")
        val byLong = SymbolId("shop.Order.total", "(long)")

        assertThat(byInt).isNotEqualTo(byLong)
    }

    @Test
    @DisplayName("Should reject a blank FQN")
    fun shouldThrowWhenFqnIsBlank() {
        assertThatIllegalArgumentException()
            .isThrownBy { SymbolId(" ") }
            .withMessageContaining("fqn must not be blank")
    }
}
