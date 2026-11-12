package io.github.skieylion.plumb.psi

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiCatchSection
import com.intellij.psi.PsiConditionalExpression
import com.intellij.psi.PsiDoWhileStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiForStatement
import com.intellij.psi.PsiForeachStatement
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiPolyadicExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.PsiSwitchBlock
import com.intellij.psi.PsiSwitchExpression
import com.intellij.psi.PsiSwitchLabelStatement
import com.intellij.psi.PsiSwitchLabeledRuleStatement
import com.intellij.psi.PsiSwitchStatement
import com.intellij.psi.PsiWhileStatement
import io.github.skieylion.plumb.model.DecisionKind
import io.github.skieylion.plumb.model.DecisionPoint

internal class DecisionPointCollector : JavaRecursiveElementWalkingVisitor() {

    private val collected = mutableListOf<DecisionPoint>()

    val decisionPoints: List<DecisionPoint>
        get() = collected.toList()

    override fun visitIfStatement(statement: PsiIfStatement) {
        record(DecisionKind.IF, statement)
        super.visitIfStatement(statement)
    }

    override fun visitForStatement(statement: PsiForStatement) {
        record(DecisionKind.LOOP, statement)
        super.visitForStatement(statement)
    }

    override fun visitForeachStatement(statement: PsiForeachStatement) {
        record(DecisionKind.LOOP, statement)
        super.visitForeachStatement(statement)
    }

    override fun visitWhileStatement(statement: PsiWhileStatement) {
        record(DecisionKind.LOOP, statement)
        super.visitWhileStatement(statement)
    }

    override fun visitDoWhileStatement(statement: PsiDoWhileStatement) {
        record(DecisionKind.LOOP, statement)
        super.visitDoWhileStatement(statement)
    }

    override fun visitConditionalExpression(expression: PsiConditionalExpression) {
        record(DecisionKind.TERNARY, expression)
        super.visitConditionalExpression(expression)
    }

    override fun visitCatchSection(section: PsiCatchSection) {
        record(DecisionKind.CATCH, section)
        super.visitCatchSection(section)
    }

    override fun visitPolyadicExpression(expression: PsiPolyadicExpression) {
        shortCircuitKindOf(expression)?.let { kind ->
            expression.operands.drop(1).forEach { operand ->
                record(kind, expression.getTokenBeforeOperand(operand) ?: operand)
            }
        }
        super.visitPolyadicExpression(expression)
    }

    override fun visitSwitchStatement(statement: PsiSwitchStatement) {
        recordSwitchBranches(statement)
        super.visitSwitchStatement(statement)
    }

    override fun visitSwitchExpression(expression: PsiSwitchExpression) {
        recordSwitchBranches(expression)
        super.visitSwitchExpression(expression)
    }

    private fun shortCircuitKindOf(expression: PsiPolyadicExpression): DecisionKind? =
        when (expression.operationTokenType) {
            JavaTokenType.ANDAND -> DecisionKind.AND
            JavaTokenType.OROR -> DecisionKind.OR
            else -> null
        }

    private fun recordSwitchBranches(block: PsiSwitchBlock) {
        val statements: Array<PsiStatement> = block.body?.statements ?: return
        var branchAnchor: PsiElement? = null
        var branchHasBody = false
        var isDefaultBranch = false
        for (statement in statements) {
            when (statement) {
                is PsiSwitchLabeledRuleStatement -> if (!statement.isDefaultCase) record(DecisionKind.CASE, statement)
                is PsiSwitchLabelStatement -> {
                    if (branchHasBody) {
                        branchAnchor?.let { record(DecisionKind.CASE, it) }
                        branchAnchor = null
                    }
                    branchAnchor = branchAnchor ?: statement
                    isDefaultBranch = statement.isDefaultCase
                    branchHasBody = false
                }

                else -> {
                    branchAnchor = branchAnchor ?: statement
                    branchHasBody = true
                }
            }
        }
        if (branchHasBody && !isDefaultBranch) {
            branchAnchor?.let { record(DecisionKind.CASE, it) }
        }
    }

    private fun record(kind: DecisionKind, anchor: PsiElement) {
        collected += DecisionPoint(kind, PsiSourceLocations.lineOf(anchor))
    }
}
