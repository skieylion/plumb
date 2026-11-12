package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.TypeNode

class HotspotRanker(private val engine: MetricEngine, private val thresholds: Thresholds) {

    fun rank(types: List<TypeNode>, metricId: String, limit: Int): HotspotReport {
        engine.requireKnown(metricId)
        require(limit > 0) { "limit must be positive: $limit" }
        val ranked = types.flatMap(TypeNode::allFunctions)
            .mapNotNull { hotspotOf(it, metricId) }
            .sortedWith(HIGHEST_VALUE_FIRST)
        return HotspotReport(
            metricId = metricId,
            threshold = thresholds.limits[metricId],
            measured = ranked.size,
            exceeding = ranked.count { it.verdict.status == Status.WARN },
            hotspots = ranked.take(limit),
        )
    }

    private fun hotspotOf(function: FunctionNode, metricId: String): Hotspot? =
        engine.measure(function, metricId)?.let { Hotspot(function.location, thresholds.judge(it)) }

    private companion object {
        val HIGHEST_VALUE_FIRST: Comparator<Hotspot> = compareByDescending<Hotspot> { it.verdict.value }
            .thenBy { it.verdict.symbol.fqn }
            .thenBy { it.verdict.symbol.signature.orEmpty() }
    }
}
