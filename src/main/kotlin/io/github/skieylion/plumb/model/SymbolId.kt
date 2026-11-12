package io.github.skieylion.plumb.model

data class SymbolId(val fqn: String, val signature: String? = null) {
    init {
        require(fqn.isNotBlank()) { "fqn must not be blank" }
    }
}
