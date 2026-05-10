package by.riewe.cadence.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class NumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Форматируем число с пробелами каждые 3 знака
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat("#,###", symbols)

        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val number = originalText.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)
        val transformedText = formatter.format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val spacesBefore = transformedText.substring(0, minOf(offset + (transformedText.length - originalText.length), transformedText.length))
                    .count { it == ' ' }
                return minOf(offset + spacesBefore, transformedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val spacesBefore = transformedText.substring(0, minOf(offset, transformedText.length))
                    .count { it == ' ' }
                return minOf(offset - spacesBefore, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}