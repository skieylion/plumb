package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Thresholds
import io.github.skieylion.plumb.metrics.Verdict
import io.github.skieylion.plumb.model.SourceLocation
import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TypeReportJsonTest {

    private val file = "/src/shop/Order.java"
    private val calculate = SymbolId("shop.Order.calculate", "(Order)")
    private val clamp = SymbolId("shop.Order.Line.clamp", "(int,int)")

    @Test
    @DisplayName("A type report is rendered as compact JSON with thresholds and per-function metrics")
    fun shouldRenderCompactJsonWhenTypeHasFunctions() {
        val report = TypeReport(
            id = SymbolId("shop.Order"),
            location = SourceLocation(file, 3, 120),
            thresholds = Thresholds(mapOf("loc" to 60.0, "cc" to 10.0)),
            functions = listOf(
                FunctionReport(
                    calculate,
                    SourceLocation(file, 40, 118),
                    listOf(
                        Verdict(calculate, "cc", 23.0, 10.0, Status.WARN),
                        Verdict(calculate, "loc", 79.0, 60.0, Status.WARN),
                    ),
                ),
                FunctionReport(
                    clamp,
                    SourceLocation(file, 100, 104),
                    listOf(Verdict(clamp, "cc", 3.0, 10.0, Status.OK)),
                ),
            ),
        )

        val json = TypeReportJson.render(report)

        assertThat(json).isEqualTo(
            """{"schemaVersion":1,"type":"shop.Order","file":"/src/shop/Order.java","lines":[3,120],""" +
                    """"status":"WARN","thresholds":{"cc":10,"loc":60},"functions":[""" +
                    """{"name":"calculate(Order)","lines":[40,118],"status":"WARN","metrics":{"cc":23,"loc":79}},""" +
                    """{"name":"Line.clamp(int,int)","lines":[100,104],"status":"OK","metrics":{"cc":3}}]}""",
        )
    }

    @Test
    @DisplayName("A type without measured functions is OK with an empty function list")
    fun shouldRenderEmptyFunctionsWhenTypeHasNoMeasuredFunctions() {
        val report =
            TypeReport(SymbolId("shop.Marker"), SourceLocation(file, 1, 2), Thresholds(emptyMap()), emptyList())

        val json = TypeReportJson.render(report)

        assertThat(json).isEqualTo(
            """{"schemaVersion":1,"type":"shop.Marker","file":"/src/shop/Order.java","lines":[1,2],""" +
                    """"status":"OK","thresholds":{},"functions":[]}""",
        )
    }
}
