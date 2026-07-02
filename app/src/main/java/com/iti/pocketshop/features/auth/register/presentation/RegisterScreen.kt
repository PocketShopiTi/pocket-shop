package com.iti.pocketshop.features.auth.register.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iti.pocketshop.R
import com.iti.pocketshop.core.networkutils.toUserMessage

@Composable
fun RegisterRoot(
    openVerification: () -> Unit,
    openLogin: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RegisterEvent.NavigateToVerification -> openVerification()
            }
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
    val context = LocalContext.current

    val creamBackground = Color(0xFFFAF7F2)
    val textCharcoal = Color(0xFF2E2A25)
    val rustBrown = Color(0xFFBD5D38)
    val mutedGrey = Color(0xFF8C8276)
    val lightGreyBorder = Color(0xFFE6DED5)
    val placeholderColor = Color(0xFFB3A89B)

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(creamBackground)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Back Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECE1))
                        .clickable {
                            navigateBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = textCharcoal,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Header Title
                Text(
                    text = "Create account",
                    fontSize = 36.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = textCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join Pocket Shop today",
                    fontSize = 16.sp,
                    color = mutedGrey
                )

                Spacer(modifier = Modifier.height(32.dp))

                // General Error Alert
                if (state.generalError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = state.generalError.toUserMessage(context),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                // Input fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // FIRST NAME Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.register_first_name_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        OutlinedTextField(
                            value = state.firstNameInput,
                            onValueChange = { onAction(RegisterAction.FirstNameChanged(it)) },
                            placeholder = {
                                Text(
                                    stringResource(R.string.register_first_name_placeholder),
                                    color = placeholderColor,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = placeholderColor
                                )
                            },
                            isError = state.firstNameError,
                            supportingText = if (state.firstNameError) {
                                { Text(stringResource(R.string.register_name_required)) }
                            } else null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = rustBrown,
                                unfocusedBorderColor = lightGreyBorder,
                                errorContainerColor = Color.White,
                                focusedTextColor = textCharcoal,
                                unfocusedTextColor = textCharcoal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // LAST NAME Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.register_last_name_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        OutlinedTextField(
                            value = state.lastNameInput,
                            onValueChange = { onAction(RegisterAction.LastNameChanged(it)) },
                            placeholder = {
                                Text(
                                    stringResource(R.string.register_last_name_placeholder),
                                    color = placeholderColor,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = placeholderColor
                                )
                            },
                            isError = state.lastNameError,
                            supportingText = if (state.lastNameError) {
                                { Text(stringResource(R.string.register_name_required)) }
                            } else null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = rustBrown,
                                unfocusedBorderColor = lightGreyBorder,
                                errorContainerColor = Color.White,
                                focusedTextColor = textCharcoal,
                                unfocusedTextColor = textCharcoal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // EMAIL Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "EMAIL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        OutlinedTextField(
                            value = state.emailInput,
                            onValueChange = { onAction(RegisterAction.EmailChanged(it)) },
                            placeholder = { Text("email@example.com", color = placeholderColor) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email Icon",
                                    tint = placeholderColor
                                )
                            },
                            isError = state.emailError,
                            supportingText = if (state.emailError) {
                                { Text(stringResource(R.string.error_invalid_email)) }
                            } else null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = rustBrown,
                                unfocusedBorderColor = lightGreyBorder,
                                errorContainerColor = Color.White,
                                focusedTextColor = textCharcoal,
                                unfocusedTextColor = textCharcoal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // PASSWORD Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        OutlinedTextField(
                            value = state.passwordInput,
                            onValueChange = { onAction(RegisterAction.PasswordChanged(it)) },
                            placeholder = {
                                Text(
                                    "Create a strong password",
                                    color = placeholderColor
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Password Icon",
                                    tint = placeholderColor
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                        tint = placeholderColor
                                    )
                                }
                            },
                            isError = state.passwordError,
                            supportingText = if (state.passwordError) {
                                { Text(stringResource(R.string.register_password_too_short)) }
                            } else null,
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = rustBrown,
                                unfocusedBorderColor = lightGreyBorder,
                                errorContainerColor = Color.White,
                                focusedTextColor = textCharcoal,
                                unfocusedTextColor = textCharcoal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Static instruction under the password field in the mockup
                        Text(
                            text = "At least 8 characters with a number",
                            fontSize = 11.sp,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // CONFIRM PASSWORD Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "CONFIRM PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedGrey,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        OutlinedTextField(
                            value = state.confirmPasswordInput,
                            onValueChange = { onAction(RegisterAction.ConfirmPasswordChanged(it)) },
                            placeholder = {
                                Text(
                                    "Repeat your password",
                                    color = placeholderColor
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Confirm Password Icon",
                                    tint = placeholderColor
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                        tint = placeholderColor
                                    )
                                }
                            },
                            isError = state.confirmPasswordError,
                            supportingText = if (state.confirmPasswordError) {
                                { Text(stringResource(R.string.register_passwords_do_not_match)) }
                            } else null,
                            singleLine = true,
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = rustBrown,
                                unfocusedBorderColor = lightGreyBorder,
                                errorContainerColor = Color.White,
                                focusedTextColor = textCharcoal,
                                unfocusedTextColor = textCharcoal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Terms and Conditions Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTermsChecked,
                        onCheckedChange = { isTermsChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = rustBrown,
                            uncheckedColor = placeholderColor,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val termsText = buildAnnotatedString {
                        append("I agree to the ")
                        pushStyle(SpanStyle(color = rustBrown, fontWeight = FontWeight.Bold))
                        append("Terms of Service")
                        pop()
                        append(" and ")
                        pushStyle(SpanStyle(color = rustBrown, fontWeight = FontWeight.Bold))
                        append("Privacy Policy")
                        pop()
                    }
                    Text(
                        text = termsText,
                        fontSize = 13.sp,
                        color = textCharcoal,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Create account button
                Button(
                    onClick = { onAction(RegisterAction.RegisterClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !state.isLoading && isTermsChecked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rustBrown,
                        disabledContainerColor = rustBrown.copy(alpha = 0.5f)
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Navigation Links
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = mutedGrey
                    )
                    Text(
                        text = "Log in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = rustBrown,
                        modifier = Modifier.clickable { openLogin() }
                    )
                }
            }
        }
    }
}

