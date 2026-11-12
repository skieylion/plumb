package io.github.skieylion.plumb.psi

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import io.github.skieylion.plumb.model.TypeNode

class SmartTypeModelReader(private val reader: PsiTypeModelReader) {

    suspend fun read(project: Project, fqn: String): TypeNode? = smartReadAction(project) {
        JavaPsiFacade.getInstance(project)
            .findClass(fqn, GlobalSearchScope.projectScope(project))
            ?.let(reader::read)
    }

    suspend fun readAll(project: Project): List<TypeNode> {
        val javaFiles = smartReadAction(project) {
            FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
                .sortedBy { it.path }
        }
        return javaFiles.flatMap { file -> smartReadAction(project) { readTopLevelTypes(project, file) } }
    }

    private fun readTopLevelTypes(project: Project, file: VirtualFile): List<TypeNode> {
        if (!file.isValid) return emptyList()
        val javaFile = PsiManager.getInstance(project).findFile(file) as? PsiJavaFile ?: return emptyList()
        return javaFile.classes.map(reader::read)
    }
}
