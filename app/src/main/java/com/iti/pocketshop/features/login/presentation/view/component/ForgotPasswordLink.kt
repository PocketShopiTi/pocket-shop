package com.iti.pocketshop.features.login.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ForgotPasswordLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.None,
            modifier = Modifier.clickableText(onClick)
        )
    }
}

 private fun Modifier.clickableText(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.let {
             Modifier
        }
    )

@Preview
@Composable
private fun ForgotPasswordLinkPreview() {
    ForgotPasswordLink(onClick = {})
}