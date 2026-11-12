package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.FunctionNode

object LinesOfCode : FunctionMetric {

    override val id = "loc"

    override fun compute(function: FunctionNode): Double? =
        if (function.hasBody) function.codeLines.toDouble() else null
}
