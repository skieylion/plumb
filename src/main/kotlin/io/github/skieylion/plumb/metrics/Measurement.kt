package io.github.skieylion.plumb.metrics

import io.github.skieylion.plumb.model.SymbolId

data class Measurement(val symbol: SymbolId, val metricId: String, val value: Double)
