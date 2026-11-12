package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.FunctionNode

object CyclomaticComplexity : FunctionMetric {

    private const val SINGLE_PATH = 1

    override val id = "cc"

    override fun compute(function: FunctionNode): Double? =
        if (function.hasBody) (SINGLE_PATH + function.decisionPoints.size).toDouble() else null
}
