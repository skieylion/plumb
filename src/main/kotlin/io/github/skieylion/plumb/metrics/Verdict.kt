package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.SymbolId

data class Verdict(
    val symbol: SymbolId,
    val metricId: String,
    val value: Double,
    val threshold: Double?,
    val status: Status,
)
