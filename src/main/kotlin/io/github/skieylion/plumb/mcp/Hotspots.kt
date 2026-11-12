package io.github.skieylion.plumb.mcp

import com.intellij.openapi.project.Project
import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.StandardMetrics
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.psi.PsiTypeModelReader
import io.github.skieylion.plumb.psi.SmartTypeModelReader
import io.github.skieylion.plumb.report.HotspotRanker
import io.github.skieylion.plumb.report.HotspotReportJson

internal class Hotspots(
    private val modelReader: SmartTypeModelReader = SmartTypeModelReader(PsiTypeModelReader()),
    private val engine: MetricEngine = MetricEngine(StandardMetrics.ALL),
    thresholds: Thresholds = Thresholds.DEFAULT,
) {

    private val ranker = HotspotRanker(engine, thresholds)

    suspend fun reportJson(project: Project, metricId: String, topN: Int): String {
        engine.requireKnown(metricId)
        require(topN in TOP_N_RANGE) { "topN must be between ${TOP_N_RANGE.first} and ${TOP_N_RANGE.last}: $topN" }
        val types = modelReader.readAll(project)
        return HotspotReportJson.render(ranker.rank(types, metricId, topN))
    }

    companion object {
        const val DEFAULT_TOP_N = 10
        private val TOP_N_RANGE = 1..50
    }
}
