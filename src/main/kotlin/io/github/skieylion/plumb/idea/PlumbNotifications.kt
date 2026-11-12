package io.github.skieylion.plumb.idea

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

internal object PlumbNotifications {

    private const val GROUP_ID = "Plumb"
    private const val TITLE = "Plumb"

    fun info(project: Project, content: String) = notify(project, content, NotificationType.INFORMATION)

    fun error(project: Project, content: String) = notify(project, content, NotificationType.ERROR)

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(TITLE, content, type)
            .notify(project)
    }
}
