package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.TypeNode

class MetricEngine(private val metrics: List<Metric>) {

    init {
        val repeatedIds = metrics.groupingBy { it.id }.eachCount().filterValues { it > 1 }.keys
        require(repeatedIds.isEmpty()) { "Metric ids must be unique, repeated: $repeatedIds" }
    }

    private val metricsById = metrics.associateBy(Metric::id)

    fun requireKnown(metricId: String) {
        require(metricId in metricsById) { "Unknown metric '$metricId', known: ${metricsById.keys}" }
    }

    fun measure(type: TypeNode): List<Measurement> = type.allFunctions().flatMap(::measure)

    fun measure(function: FunctionNode): List<Measurement> = metrics.mapNotNull { metric -> measure(metric, function) }

    fun measure(function: FunctionNode, metricId: String): Measurement? {
        requireKnown(metricId)
        return measure(metricsById.getValue(metricId), function)
    }

    private fun measure(metric: Metric, function: FunctionNode): Measurement? = when (metric) {
        is FunctionMetric -> metric.compute(function)?.let { Measurement(function.id, metric.id, it) }
    }
}
