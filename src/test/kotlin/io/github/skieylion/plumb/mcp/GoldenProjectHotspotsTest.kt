package io.github.skieylion.plumb.mcp

import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.skieylion.plumb.GoldenSources
import org.assertj.core.api.Assertions.assertThat

class GoldenProjectHotspotsTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = GoldenSources.path

    fun testShouldRankGoldenProjectByComplexityWhenMatchingReference() {
        myFixture.copyDirectoryToProject(GOLDEN_PACKAGE, GOLDEN_PACKAGE)

        val json = timeoutRunBlocking { Hotspots().reportJson(project, "cc", 3) }

        assertThat(json).startsWith("""{"schemaVersion":1,"metric":"cc","threshold":10,"measured":105,"exceeding":1,""")
        assertThat(rankedFunctionsOf(json)).containsExactly(
            "golden.shop.OrderService.calculate(Order)" to 20,
            "golden.constructs.Switches.switchExpression(DayOfWeek)" to 7,
            "golden.constructs.Switches.patternSwitch(Object)" to 6,
        )
    }

    private fun rankedFunctionsOf(json: String): List<Pair<String, Int>> =
        HOTSPOT_PATTERN.findAll(json).map { it.groupValues[1] to it.groupValues[2].toInt() }.toList()

    private companion object {
        const val GOLDEN_PACKAGE = "golden"
        val HOTSPOT_PATTERN = Regex(""""function":"([^"]+)".*?"value":(\d+)""")
    }
}
