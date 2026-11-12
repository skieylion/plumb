package io.github.skieylion.plumb.idea

import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.ide.progress.withBackgroundProgress
import io.github.skieylion.plumb.metrics.CyclomaticComplexity
import io.github.skieylion.plumb.metrics.MetricEngine
import io.github.skieylion.plumb.psi.PsiTypeModelReader
import io.github.skieylion.plumb.psi.SmartTypeModelReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.PROJECT)
class ComplexityReportService(private val project: Project, private val scope: CoroutineScope) {

    private val modelReader = SmartTypeModelReader(PsiTypeModelReader())
    private val engine = MetricEngine(listOf(CyclomaticComplexity))
    private val running = AtomicBoolean(false)

    val isRunning: Boolean
        get() = running.get()

    fun calculate() {
        val basePath = project.basePath ?: return PlumbNotifications.error(project, "Project has no base directory")
        if (!running.compareAndSet(false, true)) return
        scope.launch {
            try {
                withBackgroundProgress(project, "Plumb: calculating cyclomatic complexity") {
                    report(Path.of(basePath, REPORT_FILE_NAME))
                }
            } finally {
                running.set(false)
            }
        }
    }

    private suspend fun report(reportPath: Path) {
        val types = modelReader.readAll(project)
        val measurements = types.flatMap(engine::measure)
        try {
            withContext(Dispatchers.IO) { Files.writeString(reportPath, ComplexityReport.toCsv(measurements)) }
        } catch (e: IOException) {
            PlumbNotifications.error(project, "Cannot write $reportPath: ${e.message}")
            return
        }
        openInEditor(reportPath)
        PlumbNotifications.info(project, ComplexityReport.summarize(measurements, types.size))
    }

    private suspend fun openInEditor(reportPath: Path) {
        val reportFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(reportPath) ?: return
        withContext(Dispatchers.EDT) { FileEditorManager.getInstance(project).openFile(reportFile, true) }
    }

    private companion object {
        const val REPORT_FILE_NAME = "plumb-cc.csv"
    }
}
