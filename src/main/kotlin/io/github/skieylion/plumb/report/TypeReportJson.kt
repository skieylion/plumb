package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.model.SymbolId
import io.github.skieylion.plumb.report.SourceLocationJson.linesOf

object TypeReportJson {

    private const val SCHEMA_VERSION = 1

    fun render(report: TypeReport): String = Json.obj(
        "schemaVersion" to Json.num(SCHEMA_VERSION),
        "type" to Json.str(report.id.fqn),
        "file" to Json.str(report.location.file),
        "lines" to linesOf(report.location),
        "status" to Json.str(report.status.name),
        "thresholds" to Json.obj(report.thresholds.limits.toSortedMap().map { (id, limit) -> id to Json.num(limit) }),
        "functions" to Json.arr(report.functions.map { functionOf(it, report.id) }),
    )

    private fun functionOf(function: FunctionReport, owner: SymbolId): String = Json.obj(
        "name" to Json.str(nameOf(function.id, owner)),
        "lines" to linesOf(function.location),
        "status" to Json.str(function.status.name),
        "metrics" to Json.obj(function.verdicts.map { it.metricId to Json.num(it.value) }),
    )

    private fun nameOf(function: SymbolId, owner: SymbolId): String =
        function.fqn.removePrefix("${owner.fqn}.") + function.signature.orEmpty()
}
