package com.iti.pocketshop.features.aichat.domain.model

/**
 * Markers that wrap text meant for the AI only. Anything between them is sent to the model
 * as part of the turn but is stripped from the text shown in the chat bubble.
 *
 * Example: `Build an outfit around "Jacket"[[ctx]]product id: gid://...[[/ctx]]`
 * The user sees `Build an outfit around "Jacket"`; the AI also receives the product id.
 *
 * `[[ctx]]` is used instead of a dashed fence like `--- text ---` because the assistant
 * renders Markdown, where `---` is a horizontal rule and would collide.
 */
const val HIDDEN_CONTEXT_START = "[[ctx]]"
const val HIDDEN_CONTEXT_END = "[[/ctx]]"

data class ParsedMessage(
    val visibleText: String,
    val hiddenContext: String?,
)

/**
 * Splits [raw] into the part shown in the chat bubble and the hidden part sent to the AI only.
 * If no complete marker pair is present, the whole string is treated as visible.
 */
fun parseHiddenContext(raw: String): ParsedMessage {
    val start = raw.indexOf(HIDDEN_CONTEXT_START)
    if (start == -1) return ParsedMessage(raw.trim(), null)

    val contentStart = start + HIDDEN_CONTEXT_START.length
    val end = raw.indexOf(HIDDEN_CONTEXT_END, contentStart)
    if (end == -1) return ParsedMessage(raw.trim(), null)

    val hidden = raw.substring(contentStart, end).trim()
    val visible = (raw.substring(0, start) + raw.substring(end + HIDDEN_CONTEXT_END.length)).trim()
    return ParsedMessage(
        visibleText = visible,
        hiddenContext = hidden.ifBlank { null },
    )
}
