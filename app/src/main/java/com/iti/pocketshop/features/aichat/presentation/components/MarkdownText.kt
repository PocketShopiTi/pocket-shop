package com.iti.pocketshop.features.aichat.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp


@Composable
fun MarkdownText(
    text: String,
    color: Color
) {
    val annotated = remember(text, color) { buildSimpleMarkdown(text, color) }
    Text(
        text = annotated,
        color = color,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun buildSimpleMarkdown(
    text: String,
    baseColor: Color
): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                text.startsWith("**", i) -> {
                    val end = text.indexOf("**", i + 2)
                    if (end == -1) {
                        append(text.substring(i)); i = text.length
                    } else {
                        withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(i + 2, end))
                        }
                        i = end + 2
                    }
                }
                text.startsWith("`", i) -> {
                    val end = text.indexOf("`", i + 1)
                    if (end == -1) {
                        append(text.substring(i)); i = text.length
                    } else {
                        withStyle(
                            androidx.compose.ui.text.SpanStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                background = baseColor.copy(alpha = 0.1f)
                            )
                        ) {
                            append(text.substring(i + 1, end))
                        }
                        i = end + 1
                    }
                }
                text.startsWith("*", i) -> {
                    val end = text.indexOf("*", i + 1)
                    if (end == -1) {
                        append(text.substring(i)); i = text.length
                    } else {
                        withStyle(androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                            append(text.substring(i + 1, end))
                        }
                        i = end + 1
                    }
                }
                else -> {
                    val nextSpecial = listOf(
                        text.indexOf("**", i).let { if (it == -1) Int.MAX_VALUE else it },
                        text.indexOf("`", i).let { if (it == -1) Int.MAX_VALUE else it },
                        text.indexOf("*", i).let { if (it == -1) Int.MAX_VALUE else it },
                    ).min()
                    if (nextSpecial == Int.MAX_VALUE) {
                        append(text.substring(i)); i = text.length
                    } else {
                        append(text.substring(i, nextSpecial)); i = nextSpecial
                    }
                }
            }
        }
    }
}
