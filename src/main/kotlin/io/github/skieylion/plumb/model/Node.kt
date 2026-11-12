package io.github.skieylion.plumb.model

data class TypeNode(
    val id: SymbolId,
    val location: SourceLocation,
    val functions: List<FunctionNode>,
    val nestedTypes: List<TypeNode>,
) {

    fun allFunctions(): List<FunctionNode> = functions + nestedTypes.flatMap(TypeNode::allFunctions)
}

data class FunctionNode(
    val id: SymbolId,
    val location: SourceLocation,
    val hasBody: Boolean,
    val codeLines: Int,
    val decisionPoints: List<DecisionPoint>,
) {
    init {
        require(codeLines >= 0) { "codeLines must not be negative: $codeLines" }
    }
}
