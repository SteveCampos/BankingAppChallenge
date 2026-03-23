package com.stevecampos.core.ui.util

object EmojiSanitizer {
    private const val VARIATION_SELECTOR_16 = 0xFE0F
    private const val ZERO_WIDTH_JOINER = 0x200D
    private const val COMBINING_ENCLOSING_KEYCAP = 0x20E3

    fun removeEmoji(input: String): String {
        val builder = StringBuilder()
        var index = 0

        while (index < input.length) {
            val codePoint = input.codePointAt(index)
            if (!isEmojiCodePoint(codePoint)) {
                builder.appendCodePoint(codePoint)
            }
            index += Character.charCount(codePoint)
        }

        return builder.toString()
    }

    private fun isEmojiCodePoint(codePoint: Int): Boolean {
        return codePoint == VARIATION_SELECTOR_16 ||
            codePoint == ZERO_WIDTH_JOINER ||
            codePoint == COMBINING_ENCLOSING_KEYCAP ||
            codePoint in 0x1F1E6..0x1F1FF ||
            codePoint in 0x1F3FB..0x1F3FF ||
            codePoint in 0x1F300..0x1F5FF ||
            codePoint in 0x1F600..0x1F64F ||
            codePoint in 0x1F680..0x1F6FF ||
            codePoint in 0x1F900..0x1F9FF ||
            codePoint in 0x1FA70..0x1FAFF ||
            codePoint in 0x2600..0x26FF ||
            codePoint in 0x2700..0x27BF
    }
}
