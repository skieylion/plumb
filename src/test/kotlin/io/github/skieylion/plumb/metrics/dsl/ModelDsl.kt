package io.github.skieylion.plumb.metrics.dsl

import io.github.skieylion.plumb.model.DecisionKind
import io.github.skieylion.plumb.model.DecisionPoint
import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.SourceLocation
import io.github.skieylion.plumb.model.SymbolId
import io.github.skieylion.plumb.model.TypeNode

private val SYNTHETIC_LOCATION = SourceLocation("Synthetic.java", 1, 1)

@DslMarker
annotation class ModelDsl

fun type(fqn: String, block: TypeBuilder.() -> Unit = {}): TypeNode = TypeBuilder(fqn).apply(block).build()

@ModelDsl
class TypeBuilder(private val fqn: String) {

    private val functions = mutableListOf<FunctionNode>()
    private val nestedTypes = mutableListOf<TypeNode>()

    fun function(name: String, signature: String = "()", block: FunctionBuilder.() -> Unit = {}) {
        functions += FunctionBuilder(SymbolId("$fqn.$name", signature), hasBody = true).apply(block).build()
    }

    fun abstractFunction(name: String, signature: String = "()") {
        functions += FunctionBuilder(SymbolId("$fqn.$name", signature), hasBody = false).build()
    }

    fun nestedType(simpleName: String, block: TypeBuilder.() -> Unit = {}) {
        nestedTypes += TypeBuilder("$fqn.$simpleName").apply(block).build()
    }

    fun build(): TypeNode = TypeNode(
        id = SymbolId(fqn),
        location = SYNTHETIC_LOCATION,
        functions = functions.toList(),
        nestedTypes = nestedTypes.toList(),
    )
}

@ModelDsl
class FunctionBuilder(private val id: SymbolId, private val hasBody: Boolean) {

    private val decisionPoints = mutableListOf<DecisionPoint>()
    private var codeLines = if (hasBody) 1 else 0

    fun decision(kind: DecisionKind, count: Int = 1) {
        repeat(count) { decisionPoints += DecisionPoint(kind, SYNTHETIC_LOCATION) }
    }

    fun codeLines(count: Int) {
        codeLines = count
    }

    fun build(): FunctionNode = FunctionNode(
        id = id,
        location = SYNTHETIC_LOCATION,
        hasBody = hasBody,
        codeLines = codeLines,
        decisionPoints = decisionPoints.toList(),
    )
}
