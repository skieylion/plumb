package io.github.skieylion.plumb.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SourceLocationTest {

    @Test
    @DisplayName("Should accept a single-line range")
    fun shouldAcceptRangeWhenStartAndEndLinesAreEqual() {
        val location = SourceLocation("Foo.java", 7, 7)

        assertThat(location.endLine).isEqualTo(location.startLine)
    }

    @Test
    @DisplayName("Should reject a start line below one")
    fun shouldThrowWhenStartLineIsBelowOne() {
        assertThatIllegalArgumentException()
            .isThrownBy { SourceLocation("Foo.java", 0, 3) }
            .withMessageContaining("startLine must be positive")
    }

    @Test
    @DisplayName("Should reject an end line before the start line")
    fun shouldThrowWhenEndLinePrecedesStartLine() {
        assertThatIllegalArgumentException()
            .isThrownBy { SourceLocation("Foo.java", 5, 4) }
            .withMessageContaining("precedes startLine")
    }
}
