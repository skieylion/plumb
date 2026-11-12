package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.Verdict
import io.github.skieylion.plumb.model.SourceLocation

data class HotspotReport(
    val metricId: String,
    val threshold: Double?,
    val measured: Int,
    val exceeding: Int,
    val hotspots: List<Hotspot>,
)

data class Hotspot(val location: SourceLocation, val verdict: Verdict)
