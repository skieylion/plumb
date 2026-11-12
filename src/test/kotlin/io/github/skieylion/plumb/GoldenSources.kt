package io.github.skieylion.plumb

object GoldenSources {

    private const val PROPERTY = "plumb.goldenSources"

    val path: String
        get() = requireNotNull(System.getProperty(PROPERTY)) { "System property $PROPERTY is not set" }
}
