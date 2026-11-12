package io.github.skieylion.plumb.report

import io.github.skieylion.plumb.model.SourceLocation

internal object SourceLocationJson {

    fun linesOf(location: SourceLocation): String =
        Json.arr(listOf(Json.num(location.startLine), Json.num(location.endLine)))
}
