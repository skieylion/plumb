package io.github.skieylion.plumb.metrics

data class Thresholds(val limits: Map<String, Double>) {

    init {
        val invalidLimits = limits.filterValues { !it.isFinite() || it < 0 }
        require(invalidLimits.isEmpty()) { "Threshold limits must be finite and non-negative: $invalidLimits" }
    }

    fun judge(measurement: Measurement): Verdict {
        val limit = limits[measurement.metricId]
        val status = if (limit != null && measurement.value > limit) Status.WARN else Status.OK
        return Verdict(measurement.symbol, measurement.metricId, measurement.value, limit, status)
    }

    companion object {
        private const val DEFAULT_CC_LIMIT = 10.0
        private const val DEFAULT_LOC_LIMIT = 60.0

        val DEFAULT = Thresholds(
            mapOf(CyclomaticComplexity.id to DEFAULT_CC_LIMIT, LinesOfCode.id to DEFAULT_LOC_LIMIT),
        )
    }
}
