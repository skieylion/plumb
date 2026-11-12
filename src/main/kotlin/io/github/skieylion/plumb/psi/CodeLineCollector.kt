package io.github.skieylion.plumb.psi

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.PsiWhiteSpace

internal class CodeLineCollector : PsiRecursiveElementWalkingVisitor() {

    private val lines = mutableSetOf<Int>()

    val codeLines: Int
        get() = lines.size

    override fun visitElement(element: PsiElement) {
        when {
            element is PsiComment || element is PsiWhiteSpace || element.textLength == 0 -> return
            element.firstChild == null -> lines += PsiSourceLocations.linesOf(element)
            else -> super.visitElement(element)
        }
    }
}
