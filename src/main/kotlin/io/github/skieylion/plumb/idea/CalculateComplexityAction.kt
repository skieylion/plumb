package io.github.skieylion.plumb.idea

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware

class CalculateComplexityAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabled = project != null && !project.service<ComplexityReportService>().isRunning
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.service<ComplexityReportService>()?.calculate()
    }
}
