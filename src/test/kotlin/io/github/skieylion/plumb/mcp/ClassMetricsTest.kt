package io.github.skieylion.plumb.mcp

import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat

class ClassMetricsTest : BasePlatformTestCase() {

    private val classMetrics = ClassMetrics()

    fun testShouldRenderMethodMetricsAndVerdictsWhenClassExists() {
        val file = myFixture.addFileToProject("shop/Order.java", ORDER_SOURCE).virtualFile.path

        val json = timeoutRunBlocking { classMetrics.reportJson(project, "shop.Order") }

        assertThat(json).isEqualTo(
            """{"schemaVersion":1,"type":"shop.Order","file":"$file","lines":[3,20],"status":"WARN",""" +
                    """"thresholds":{"cc":10,"loc":60},"functions":[""" +
                    """{"name":"discount(int,boolean)","lines":[4,9],"status":"OK","metrics":{"cc":3,"loc":6}},""" +
                    """{"name":"isSpecial(int)","lines":[11,13],"status":"WARN","metrics":{"cc":11,"loc":3}},""" +
                    """{"name":"Line.clamp(int)","lines":[16,18],"status":"OK","metrics":{"cc":2,"loc":3}}]}""",
        )
    }

    fun testShouldReturnNullWhenClassIsNotInProject() {
        myFixture.addFileToProject("shop/Order.java", ORDER_SOURCE)

        val json = timeoutRunBlocking { classMetrics.reportJson(project, "shop.Missing") }

        assertThat(json).isNull()
    }

    private companion object {
        val ORDER_SOURCE = """
            package shop;

            public class Order {
                public int discount(int total, boolean gold) {
                    if (total > 100 && gold) {
                        return 10;
                    }
                    return 0;
                }

                boolean isSpecial(int v) {
                    return v == 1 || v == 2 || v == 3 || v == 4 || v == 5 || v == 6 || v == 7 || v == 8 || v == 9 || v == 10 || v == 11;
                }

                static class Line {
                    int clamp(int value) {
                        return value < 0 ? 0 : value;
                    }
                }
            }
        """.trimIndent()
    }
}
