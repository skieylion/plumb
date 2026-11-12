package io.github.skieylion.plumb.mcp

import com.intellij.mcpserver.McpToolset
import com.intellij.mcpserver.annotations.McpDescription
import com.intellij.mcpserver.annotations.McpTool
import com.intellij.mcpserver.mcpFail
import com.intellij.mcpserver.project
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.currentCoroutineContext
import kotlin.time.measureTimedValue

class PlumbMcpToolset : McpToolset {

    private val metrics = ClassMetrics()
    private val hotspots = Hotspots()

    @McpTool(name = CLASS_METRICS_TOOL)
    @McpDescription(CLASS_METRICS_DESCRIPTION)
    suspend fun classMetrics(@McpDescription(FQN_DESCRIPTION) fqn: String): String {
        val project = currentCoroutineContext().project
        val (json, elapsed) = measureTimedValue { metrics.reportJson(project, fqn) }
        LOG.info("$CLASS_METRICS_TOOL fqn=$fqn found=${json != null} chars=${json?.length ?: 0} time=$elapsed")
        return json ?: mcpFail("Class '$fqn' is not found in the project sources")
    }

    @McpTool(name = HOTSPOTS_TOOL)
    @McpDescription(HOTSPOTS_DESCRIPTION)
    suspend fun hotspots(
        @McpDescription(METRIC_DESCRIPTION) metric: String,
        @McpDescription(TOP_N_DESCRIPTION) topN: Int = Hotspots.DEFAULT_TOP_N,
    ): String {
        val project = currentCoroutineContext().project
        val (json, elapsed) = measureTimedValue {
            try {
                hotspots.reportJson(project, metric, topN)
            } catch (e: IllegalArgumentException) {
                LOG.info("$HOTSPOTS_TOOL metric=$metric topN=$topN rejected: ${e.message}")
                mcpFail(e.message ?: "Invalid arguments: metric=$metric topN=$topN")
            }
        }
        LOG.info("$HOTSPOTS_TOOL metric=$metric topN=$topN chars=${json.length} time=$elapsed")
        return json
    }

    private companion object {
        val LOG = logger<PlumbMcpToolset>()

        const val CLASS_METRICS_TOOL = "class_metrics"
        const val CLASS_METRICS_DESCRIPTION =
            "Measures a Java class from the project sources with IDE-level precision and returns compact JSON: " +
                    "the thresholds applied, and for every method with a body (nested classes included) its line range, " +
                    "metric values (cc = cyclomatic complexity, loc = lines containing code, " +
                    "without blank and comment-only lines) and status OK or WARN when a value exceeds its threshold. " +
                    "Call it before and after a refactoring to prove the change reduced complexity."
        const val FQN_DESCRIPTION = "Fully qualified name of the class, e.g. com.shop.order.OrderService; " +
                "nested classes use dots: com.shop.order.OrderService.Line"

        const val HOTSPOTS_TOOL = "hotspots"
        const val HOTSPOTS_DESCRIPTION =
            "Ranks every Java method with a body in the project sources by one metric and returns compact JSON: " +
                    "the threshold applied, how many methods were measured and how many exceed the threshold, " +
                    "and the top methods by descending value with fully qualified name and signature, file, " +
                    "line range, value and status OK or WARN. " +
                    "Call it first to find the worst places, then class_metrics on their classes to drill down."
        const val METRIC_DESCRIPTION =
            "Metric to rank by: cc = cyclomatic complexity, loc = lines containing code"
        const val TOP_N_DESCRIPTION = "How many top methods to return, from 1 to 50"
    }
}
