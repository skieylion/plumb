package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.TypeNode

class TypeReporter(private val engine: MetricEngine, private val thresholds: Thresholds) {

    fun report(type: TypeNode): TypeReport = TypeReport(
        id = type.id,
        location = type.location,
        thresholds = thresholds,
        functions = type.allFunctions().mapNotNull(::reportOf),
    )

    private fun reportOf(function: FunctionNode): FunctionReport? {
        val verdicts = engine.measure(function).map(thresholds::judge)
        return if (verdicts.isEmpty()) null else FunctionReport(function.id, function.location, verdicts)
    }
}
