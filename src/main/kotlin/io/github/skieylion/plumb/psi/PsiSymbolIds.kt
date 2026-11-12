package io.github.skieylion.plumb.psi

import com.intellij.psi.PsiMethod
import io.github.skieylion.plumb.model.SymbolId

internal object PsiSymbolIds {

    fun ofMethod(method: PsiMethod, ownerFqn: String): SymbolId = SymbolId(
        fqn = "$ownerFqn.${method.name}",
        signature = method.parameterList.parameters.joinToString(",", "(", ")") { it.type.presentableText },
    )
}
