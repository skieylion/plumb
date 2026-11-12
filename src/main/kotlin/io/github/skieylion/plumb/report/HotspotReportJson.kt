package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.report.SourceLocationJson.linesOf

object HotspotReportJson {

    private const val SCHEMA_VERSION = 1

    fun render(report: HotspotReport): String = Json.obj(
        listOfNotNull(
            "schemaVersion" to Json.num(SCHEMA_VERSION),
            "metric" to Json.str(report.metricId),
            report.threshold?.let { "threshold" to Json.num(it) },
            "measured" to Json.num(report.measured),
            "exceeding" to Json.num(report.exceeding),
            "hotspots" to Json.arr(report.hotspots.map(::hotspotOf)),
        ),
    )

    private fun hotspotOf(hotspot: Hotspot): String = Json.obj(
        "function" to Json.str(hotspot.verdict.symbol.fqn + hotspot.verdict.symbol.signature.orEmpty()),
        "file" to Json.str(hotspot.location.file),
        "lines" to linesOf(hotspot.location),
        "value" to Json.num(hotspot.verdict.value),
        "status" to Json.str(hotspot.verdict.status.name),
    )
}
