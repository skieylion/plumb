package io.github.skieylion.plumb.mcp

import com.intellij.openapi.project.Project
import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.metrics.StandardMetrics
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.psi.PsiTypeModelReader
import io.github.skieylion.plumb.psi.SmartTypeModelReader
import io.github.skieylion.plumb.report.TypeReportJson
import io.github.skieylion.plumb.report.TypeReporter

internal class ClassMetrics(
    private val modelReader: SmartTypeModelReader = SmartTypeModelReader(PsiTypeModelReader()),
    private val reporter: TypeReporter = TypeReporter(MetricEngine(StandardMetrics.ALL), Thresholds.DEFAULT),
) {

    suspend fun reportJson(project: Project, fqn: String): String? =
        modelReader.read(project, fqn)?.let { TypeReportJson.render(reporter.report(it)) }
}
