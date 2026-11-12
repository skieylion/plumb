package io.github.skieylion.plumb.psi

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.skieylion.plumb.model.SourceLocation

internal object PsiSourceLocations {

    fun spanOf(element: PsiElement): SourceLocation {
        val lines = linesOf(element)
        return SourceLocation(pathOf(element.containingFile), lines.first, lines.last)
    }

    fun linesOf(element: PsiElement): IntRange {
        val document = documentOf(element.containingFile)
        val range = element.textRange
        return document.getLineNumber(range.startOffset) + 1..document.getLineNumber(range.endOffset) + 1
    }

    fun lineOf(element: PsiElement): SourceLocation {
        val file = element.containingFile
        val line = documentOf(file).getLineNumber(element.textRange.startOffset) + 1
        return SourceLocation(pathOf(file), line, line)
    }

    private fun documentOf(file: PsiFile): Document =
        checkNotNull(file.viewProvider.document) { "No document for ${file.name}" }

    private fun pathOf(file: PsiFile): String = file.virtualFile?.path ?: file.name
}
