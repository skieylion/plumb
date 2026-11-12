package io.github.skieylion.plumb.psi

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.skieylion.plumb.GoldenSources
import io.github.skieylion.plumb.model.TypeNode
import org.assertj.core.api.Assertions.assertThat

class PsiTypeModelReaderCodeLinesTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = GoldenSources.path

    fun testShouldCountOnlyLinesWithCodeWhenMethodsMixCodeCommentsAndBlankLines() {
        myFixture.copyFileToProject(LOC_CASES_FILE)

        val type = readType(LOC_CASES_FQN)

        assertThat(type.functions.map { it.id.fqn.substringAfterLast('.') to it.codeLines })
            .containsExactly(
                "compactBody" to 1,
                "withBlankLines" to 5,
                "withLineComments" to 4,
                "withJavadoc" to 3,
                "withBlockComment" to 4,
                "multilineStatement" to 9,
                "textBlock" to 7,
                "braceOnOwnLine" to 8,
                "commentedOutCode" to 3,
                "annotatedMethod" to 3,
            )
    }

    fun testShouldCountDeclarationLineWhenMethodIsAbstract() {
        myFixture.addFileToProject(
            "shop/Shape.java",
            "package shop;\n\npublic interface Shape {\n    double area();\n}\n"
        )

        val area = readType("shop.Shape").functions.single()

        assertThat(area.hasBody).isFalse()
        assertThat(area.codeLines).isEqualTo(1)
    }

    private fun readType(fqn: String): TypeNode {
        val psiClass = JavaPsiFacade.getInstance(project).findClass(fqn, GlobalSearchScope.projectScope(project))
        return PsiTypeModelReader().read(requireNotNull(psiClass) { "Class $fqn is not in the test project" })
    }

    private companion object {
        const val LOC_CASES_FILE = "golden/constructs/LocCases.java"
        const val LOC_CASES_FQN = "golden.constructs.LocCases"
    }
}
