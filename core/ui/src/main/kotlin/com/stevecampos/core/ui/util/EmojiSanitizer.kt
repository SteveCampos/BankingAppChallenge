package com.stevecampos.core.ui.util

object EmojiSanitizer {
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
        val block = Character.UnicodeBlock.of(codePoint)
        return codePoint == 0xFE0F ||
            codePoint in 0x1F1E6..0x1F1FF ||
            codePoint in 0x1F3FB..0x1F3FF ||
            block == Character.UnicodeBlock.EMOTICONS ||
            block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS ||
            block == Character.UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS ||
            block == Character.UnicodeBlock.SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS ||
            block == Character.UnicodeBlock.SYMBOLS_AND_PICTOGRAPHS_EXTENDED_A ||
            block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS ||
            block == Character.UnicodeBlock.DINGBATS
    }
}
