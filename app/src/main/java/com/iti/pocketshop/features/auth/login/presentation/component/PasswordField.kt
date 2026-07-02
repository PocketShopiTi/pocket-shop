package com.iti.pocketshop.features.auth.login.presentation.component

import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.res.stringResource
import com.iti.pocketshop.R

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.login_label_password),
    placeholder: String = stringResource(id = R.string.login_placeholder_password),
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    AuthTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        isError = isError,
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Password,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    painter = if (isPasswordVisible) painterResource(id = R.drawable.ic_visibility)
                    else painterResource(id = R.drawable.ic_visibility_off),
                    contentDescription = if (isPasswordVisible)
                        stringResource(id = R.string.login_content_desc_hide_password)
                    else
                        stringResource(id = R.string.login_content_desc_show_password)
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None
        else PasswordVisualTransformation()
    )
}

@Preview
@Composable
private fun PasswordFieldPreview() {
    PasswordField(value = "", onValueChange = {})
}
