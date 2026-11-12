package io.github.skieylion.plumb.model

enum class DecisionKind { IF, LOOP, CASE, CATCH, TERNARY, AND, OR }

data class DecisionPoint(val kind: DecisionKind, val location: SourceLocation)
