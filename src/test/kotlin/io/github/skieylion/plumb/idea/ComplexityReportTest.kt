package io.github.skieylion.plumb.idea

import io.github.skieylion.plumb.metrics.Measurement
import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ComplexityReportTest {

    private val calculate = Measurement(SymbolId("golden.shop.OrderService.calculate", "(Order)"), "cc", 20.0)
    private val clamp = Measurement(SymbolId("golden.Nested.clamp", "(int,int,int)"), "cc", 3.0)

    @Test
    fun shouldRenderQuotedMethodNamesAndIntegerValuesWhenWritingCsv() {
        val csv = ComplexityReport.toCsv(listOf(calculate, clamp))

        assertThat(csv).isEqualTo(
            "Method,v(G)\n" +
                    "\"golden.shop.OrderService.calculate(Order)\",20\n" +
                    "\"golden.Nested.clamp(int,int,int)\",3\n",
        )
    }

    @Test
    fun shouldRenderOnlyHeaderWhenThereAreNoMeasurements() {
        val csv = ComplexityReport.toCsv(emptyList())

        assertThat(csv).isEqualTo("Method,v(G)\n")
    }

    @Test
    fun shouldKeepFractionWhenValueIsNotWhole() {
        val average = Measurement(SymbolId("golden.Avg.mean", "()"), "avg", 2.5)

        val csv = ComplexityReport.toCsv(listOf(average))

        assertThat(csv).endsWith("\"golden.Avg.mean()\",2.5\n")
    }

    @Test
    fun shouldNameMostComplexMethodWhenSummarizing() {
        val summary = ComplexityReport.summarize(listOf(clamp, calculate), typeCount = 2)

        assertThat(summary).isEqualTo(
            "Measured 2 methods in 2 types. Most complex: golden.shop.OrderService.calculate(Order) (CC 20)",
        )
    }

    @Test
    fun shouldReportNoMethodsWhenSummarizingEmptyMeasurements() {
        val summary = ComplexityReport.summarize(emptyList(), typeCount = 0)

        assertThat(summary).isEqualTo("No Java methods found in the project")
    }
}
