package com.iti.pocketshop.features.register.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.features.register.presentation.action.RegisterAction
import com.iti.pocketshop.features.register.presentation.state.RegisterState
import com.iti.pocketshop.features.register.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterRoot(
    openHome: () -> Unit,
    openLogin: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            openHome()
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        openLogin = openLogin,
        navigateBack = navigateBack,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    openLogin: () -> Unit,
    navigateBack: () -> Unit,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isTermsChecked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.create_account),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.join_millions_of_online_shoppers),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.full_name),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    OutlinedTextField(
                        value = state.fullNameInput,
                        onValueChange = { onAction(RegisterAction.FullNameChanged(it)) },
                        placeholder = { Text(stringResource(R.string.enter_your_name)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_profile),
                                contentDescription = null,
                            )
                        },
                        isError = state.isFullNameError,
                        supportingText = {
                            Text(
                                stringResource(
                                    if (state.isFullNameError) {
                                        R.string.please_enter_your_full_name
                                    } else R.string.empty_string
                                )
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.15f
                            ),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.email),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    OutlinedTextField(
                        value = state.emailInput,
                        onValueChange = { onAction(RegisterAction.EmailChanged(it)) },
                        placeholder = { Text(stringResource(R.string.enter_email)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                                contentDescription = null
                            )
                        },
                        isError = state.isEmailError,
                        supportingText = {
                            Text(
                                stringResource(
                                    if (state.isEmailError) {
                                        R.string.please_enter_a_valid_email
                                    } else R.string.empty_string
                                )
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.15f
                            ),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.password),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    OutlinedTextField(
                        value = state.passwordInput,
                        onValueChange = { onAction(RegisterAction.PasswordChanged(it)) },
                        placeholder = { Text(stringResource(R.string.enter_a_strong_password)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
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
                        isError = state.isPasswordError,
                        supportingText = {
                            Text(
                                stringResource(
                                    if (state.isPasswordError) {
                                        R.string.error_password_too_short
                                    } else R.string.empty_string
                                )
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.15f
                            ),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.confirm_password),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    OutlinedTextField(
                        value = state.confirmPasswordInput,
                        onValueChange = { onAction(RegisterAction.ConfirmPasswordChanged(it)) },
                        placeholder = { Text(stringResource(R.string.repeat_your_password)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                isConfirmPasswordVisible = !isConfirmPasswordVisible
                            }) {
                                Icon(
                                    painter = if (isConfirmPasswordVisible) painterResource(id = R.drawable.ic_visibility)
                                    else painterResource(id = R.drawable.ic_visibility_off),
                                    contentDescription = if (isConfirmPasswordVisible)
                                        stringResource(id = R.string.login_content_desc_hide_password)
                                    else
                                        stringResource(id = R.string.login_content_desc_show_password)
                                )
                            }
                        },
                        isError = state.isConfirmPasswordError,
                        supportingText = {
                            Text(
                                stringResource(
                                    if (state.isConfirmPasswordError) {
                                        R.string.passwords_must_match
                                    } else R.string.empty_string
                                )
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.15f
                            ),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTermsChecked,
                        onCheckedChange = { isTermsChecked = it },
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FlowRow {
                        Text(
                            text = stringResource(R.string.i_agree_to_the),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = stringResource(R.string.terms_of_service),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    //todo
                                }
                        )
                        Text(
                            text = stringResource(R.string.and),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = stringResource(R.string.privacy_policy),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    //todo
                                }
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { onAction(RegisterAction.RegisterClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !state.isLoading && isTermsChecked,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.create_account),
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.already_have_an_account),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = stringResource(R.string.sign_in),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { openLogin() }
                    )
                }
            }
        }
    }
}

