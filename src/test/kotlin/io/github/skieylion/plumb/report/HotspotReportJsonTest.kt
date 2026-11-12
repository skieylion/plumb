package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.metrics.Status
import io.github.skieylion.plumb.metrics.Verdict
import io.github.skieylion.plumb.model.SourceLocation
import io.github.skieylion.plumb.model.SymbolId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HotspotReportJsonTest {

    private val file = "/src/shop/OrderService.java"
    private val calculate = SymbolId("shop.OrderService.calculate", "(Order)")
    private val clamp = SymbolId("shop.OrderService.Line.clamp", "(int)")

    @Test
    @DisplayName("A hotspot report is rendered as compact JSON with totals and ranked functions")
    fun shouldRenderCompactJsonWhenReportHasThreshold() {
        val report = HotspotReport(
            metricId = "cc",
            threshold = 10.0,
            measured = 104,
            exceeding = 1,
            hotspots = listOf(
                Hotspot(SourceLocation(file, 40, 118), Verdict(calculate, "cc", 20.0, 10.0, Status.WARN)),
                Hotspot(SourceLocation(file, 130, 134), Verdict(clamp, "cc", 3.0, 10.0, Status.OK)),
            ),
        )

        val json = HotspotReportJson.render(report)

        assertThat(json).isEqualTo(
            """{"schemaVersion":1,"metric":"cc","threshold":10,"measured":104,"exceeding":1,"hotspots":[""" +
                    """{"function":"shop.OrderService.calculate(Order)","file":"/src/shop/OrderService.java",""" +
                    """"lines":[40,118],"value":20,"status":"WARN"},""" +
                    """{"function":"shop.OrderService.Line.clamp(int)","file":"/src/shop/OrderService.java",""" +
                    """"lines":[130,134],"value":3,"status":"OK"}]}""",
        )
    }

    @Test
    @DisplayName("A report without a threshold omits it")
    fun shouldOmitThresholdWhenMetricHasNoLimit() {
        val report = HotspotReport("loc", threshold = null, measured = 0, exceeding = 0, hotspots = emptyList())

        val json = HotspotReportJson.render(report)

        assertThat(json).isEqualTo("""{"schemaVersion":1,"metric":"loc","measured":0,"exceeding":0,"hotspots":[]}""")
    }
}
