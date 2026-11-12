package io.github.skieylion.plumb.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TypeNodeTest {

    private val location = SourceLocation("Order.java", 1, 1)

    @Test
    @DisplayName("Own functions come first, then functions of nested types depth-first")
    fun shouldListOwnFunctionsBeforeNestedOnesWhenTypeHasNestedTypes() {
        val clamp = function("shop.Order.Line.Price.clamp")
        val total = function("shop.Order.Line.total")
        val place = function("shop.Order.place")
        val price = type("shop.Order.Line.Price", functions = listOf(clamp))
        val line = type("shop.Order.Line", functions = listOf(total), nestedTypes = listOf(price))
        val order = type("shop.Order", functions = listOf(place), nestedTypes = listOf(line))

        val functions = order.allFunctions()

        assertThat(functions).containsExactly(place, total, clamp)
    }

    @Test
    @DisplayName("A type without functions anywhere has none")
    fun shouldReturnEmptyListWhenNoTypeHasFunctions() {
        val order = type("shop.Order", nestedTypes = listOf(type("shop.Order.Marker")))

        val functions = order.allFunctions()

        assertThat(functions).isEmpty()
    }

    private fun type(
        fqn: String,
        functions: List<FunctionNode> = emptyList(),
        nestedTypes: List<TypeNode> = emptyList(),
    ) = TypeNode(SymbolId(fqn), location, functions, nestedTypes)

    private fun function(fqn: String) = FunctionNode(SymbolId(fqn, "()"), location, true, 1, emptyList())
}
