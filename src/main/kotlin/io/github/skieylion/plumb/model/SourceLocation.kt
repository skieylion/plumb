package io.github.skieylion.plumb.model

data class SourceLocation(val file: String, val startLine: Int, val endLine: Int) {
    init {
        require(startLine >= 1) { "startLine must be positive: $startLine" }
        require(endLine >= startLine) { "endLine $endLine precedes startLine $startLine" }
    }
}
