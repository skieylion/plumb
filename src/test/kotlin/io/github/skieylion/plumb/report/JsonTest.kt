package io.github.skieylion.plumb.report

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JsonTest {

    @Test
    @DisplayName("Quotes, backslashes and control characters are escaped")
    fun shouldEscapeSpecialCharactersWhenRenderingString() {
        val json = Json.str("C:\\src\t\"A\"\n\u0001")

        assertThat(json).isEqualTo("\"C:\\\\src\\t\\\"A\\\"\\n\\u0001\"")
    }

    @Test
    @DisplayName("Whole numbers are rendered without a fraction")
    fun shouldDropFractionWhenNumberIsWhole() {
        val json = Json.num(23.0)

        assertThat(json).isEqualTo("23")
    }

    @Test
    @DisplayName("Fractional numbers keep their fraction")
    fun shouldKeepFractionWhenNumberIsNotWhole() {
        val json = Json.num(2.5)

        assertThat(json).isEqualTo("2.5")
    }

    @Test
    @DisplayName("Non-finite numbers cannot be rendered")
    fun shouldThrowWhenNumberIsNotFinite() {
        assertThatIllegalArgumentException()
            .isThrownBy { Json.num(Double.POSITIVE_INFINITY) }
            .withMessageContaining("Infinity")
    }

    @Test
    @DisplayName("Objects keep member order and nest arrays")
    fun shouldRenderMembersInGivenOrderWhenBuildingObject() {
        val json = Json.obj("b" to Json.num(1), "a" to Json.arr(listOf(Json.str("x"), Json.num(2))))

        assertThat(json).isEqualTo("{\"b\":1,\"a\":[\"x\",2]}")
    }

    @Test
    @DisplayName("Empty objects and arrays are rendered as empty brackets")
    fun shouldRenderEmptyBracketsWhenThereAreNoMembers() {
        val json = Json.obj("o" to Json.obj(emptyList()), "a" to Json.arr(emptyList()))

        assertThat(json).isEqualTo("{\"o\":{},\"a\":[]}")
    }
}
