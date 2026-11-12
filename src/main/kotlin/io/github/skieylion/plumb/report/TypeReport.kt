package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.metrics.Verdict
import io.github.skieylion.plumb.model.SourceLocation
import io.github.skieylion.plumb.model.SymbolId

data class TypeReport(
    val id: SymbolId,
    val location: SourceLocation,
    val thresholds: Thresholds,
    val functions: List<FunctionReport>,
) {
    val status: Status
        get() = Status.worstOf(functions.map(FunctionReport::status))
}

data class FunctionReport(
    val id: SymbolId,
    val location: SourceLocation,
    val verdicts: List<Verdict>,
) {
    val status: Status
        get() = Status.worstOf(verdicts.map(Verdict::status))
}
