package io.github.skieylion.plumb.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.util.concurrency.annotations.RequiresReadLock
import io.github.skieylion.plumb.model.FunctionNode
import io.github.skieylion.plumb.model.SymbolId
import io.github.skieylion.plumb.model.TypeNode

class PsiTypeModelReader {

    @RequiresReadLock
    fun read(psiClass: PsiClass): TypeNode {
        val fqn = requireNotNull(psiClass.qualifiedName) { "Only named classes can be modelled: $psiClass" }
        return TypeNode(
            id = SymbolId(fqn),
            location = PsiSourceLocations.spanOf(psiClass),
            functions = psiClass.methods.filter { it.isPhysical }.map { readFunction(it, fqn) },
            nestedTypes = psiClass.innerClasses.map(::read),
        )
    }

    private fun readFunction(method: PsiMethod, ownerFqn: String): FunctionNode {
        val decisionPointCollector = DecisionPointCollector().also(method::accept)
        val codeLineCollector = CodeLineCollector().also(method::accept)
        return FunctionNode(
            id = PsiSymbolIds.ofMethod(method, ownerFqn),
            location = PsiSourceLocations.spanOf(method),
            hasBody = method.body != null,
            codeLines = codeLineCollector.codeLines,
            decisionPoints = decisionPointCollector.decisionPoints,
        )
    }
}
