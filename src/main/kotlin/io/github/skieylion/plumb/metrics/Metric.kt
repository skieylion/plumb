package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.FunctionNode

sealed interface Metric {
    val id: String
}

interface FunctionMetric : Metric {
    fun compute(function: FunctionNode): Double?
}
