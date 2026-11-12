package io.github.skieylion.plumb.mcp

import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException

class HotspotsTest : BasePlatformTestCase() {

    private val hotspots = Hotspots()

    fun testShouldRankMethodsAcrossProjectWhenMetricIsKnown() {
        val order = myFixture.addFileToProject("shop/Order.java", ORDER_SOURCE).virtualFile.path
        val shape = myFixture.addFileToProject("shop/Shape.java", SHAPE_SOURCE).virtualFile.path

        val json = timeoutRunBlocking { hotspots.reportJson(project, "cc", 2) }

        assertThat(json).isEqualTo(
            """{"schemaVersion":1,"metric":"cc","threshold":10,"measured":3,"exceeding":1,"hotspots":[""" +
                    """{"function":"shop.Order.isSpecial(int)","file":"$order",""" +
                    """"lines":[8,11],"value":11,"status":"WARN"},""" +
                    """{"function":"shop.Shape.size(int)","file":"$shape","lines":[6,8],"value":2,"status":"OK"}]}""",
        )
    }

    fun testShouldRejectUnknownMetricWhenRanking() {
        assertThatIllegalArgumentException()
            .isThrownBy { timeoutRunBlocking { hotspots.reportJson(project, "cbo", 10) } }
            .withMessage("Unknown metric 'cbo', known: [cc, loc]")
    }

    fun testShouldRejectTopNWhenOutOfRange() {
        assertThatIllegalArgumentException()
            .isThrownBy { timeoutRunBlocking { hotspots.reportJson(project, "loc", 51) } }
            .withMessage("topN must be between 1 and 50: 51")
    }

    private companion object {
        val ORDER_SOURCE = """
            package shop;

            public class Order {
                public int total(int price) {
                    return price;
                }

                boolean isSpecial(int v) {
                    return v == 1 || v == 2 || v == 3 || v == 4 || v == 5 || v == 6
                        || v == 7 || v == 8 || v == 9 || v == 10 || v == 11;
                }
            }
        """.trimIndent()

        val SHAPE_SOURCE = """
            package shop;

            public abstract class Shape {
                abstract double area();

                int size(int side) {
                    return side > 0 ? side : 0;
                }
            }
        """.trimIndent()
    }
}
