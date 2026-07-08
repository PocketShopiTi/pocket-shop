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

/**
 * Markers the assistant uses to offer tappable choices, e.g.
 * `Which occasion?[[options]]Work|Party|Casual[[/options]]`.
 * The block is stripped from the rendered bubble; the options become quick-reply chips.
 */
const val OPTIONS_START = "[[options]]"
const val OPTIONS_END = "[[/options]]"

private const val MAX_QUICK_REPLIES = 4

data class ParsedOptions(
    val visibleText: String,
    val options: List<String>,
)

/**
 * Splits [raw] into the text shown in the bubble and the list of quick-reply options.
 * Options are pipe-separated, trimmed, de-blanked, and capped at [MAX_QUICK_REPLIES].
 * If no complete marker pair is present, the whole string is visible and options are empty.
 */
fun parseQuickReplies(raw: String): ParsedOptions {
    val start = raw.indexOf(OPTIONS_START)
    if (start == -1) return ParsedOptions(raw.trim(), emptyList())

    val contentStart = start + OPTIONS_START.length
    val end = raw.indexOf(OPTIONS_END, contentStart)
    if (end == -1) return ParsedOptions(raw.trim(), emptyList())

    val options = raw.substring(contentStart, end)
        .split("|")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .take(MAX_QUICK_REPLIES)
    val visible = (raw.substring(0, start) + raw.substring(end + OPTIONS_END.length)).trim()
    return ParsedOptions(visibleText = visible, options = options)
}
