package io.github.skieylion.plumb.idea

import io.github.skieylion.plumb.metrics.Measurement
import io.github.skieylion.plumb.model.SymbolId

internal object ComplexityReport {

    private const val CSV_HEADER = "Method,v(G)"

    fun toCsv(measurements: List<Measurement>): String =
        (listOf(CSV_HEADER) + measurements.map { "\"${displayNameOf(it.symbol)}\",${formatValue(it.value)}" })
            .joinToString(separator = "\n", postfix = "\n")

    fun summarize(measurements: List<Measurement>, typeCount: Int): String {
        val mostComplex = measurements.maxByOrNull { it.value } ?: return "No Java methods found in the project"
        return "Measured ${measurements.size} methods in $typeCount types. " +
                "Most complex: ${displayNameOf(mostComplex.symbol)} (CC ${formatValue(mostComplex.value)})"
    }

    private fun displayNameOf(symbol: SymbolId): String = symbol.fqn + symbol.signature.orEmpty()

    private fun formatValue(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
}
