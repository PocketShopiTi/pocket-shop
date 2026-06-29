package com.iti.pocketshop.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.features.profile.presentation.components.ErrorContent
import com.iti.pocketshop.features.profile.presentation.components.LoadingContent

@Composable
fun ProfileRoot(
    openSettings: () -> Unit,
    openOrders: () -> Unit = {},
    openAddresses: () -> Unit = {},
    openWishList: () -> Unit = {},
    logout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null || state.user == null -> ErrorContent(
            onRetry = {
                viewModel.onAction(ProfileAction.Retry)
            }
        )

        else -> ProfileScreen(
            openSettings = openSettings,
            openOrders = openOrders,
            openAddresses = openAddresses,
            openWishList = openWishList,
            logout = logout,
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun ProfileScreen(
    openSettings: () -> Unit,
    openOrders: () -> Unit = {},
    openAddresses: () -> Unit = {},
    openWishList: () -> Unit = {},
    logout: () -> Unit,
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.size(12.5.dp)
        )
        UserInfoRow(state.user!!)

        Box(

        ) {
            Row() {
                Column() {
                }
            }
        }
    }
}

@Composable
fun UserInfoRow(user: UserEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.67.dp,
                    MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            if (user.imageUrl != null) {
                AsyncImage(
                    model = user.imageUrl,
                    contentDescription = "User Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)

                )
            } else {
                Text(
                    text = user.name.take(1).uppercase(),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Column() {
            Text(text = "Welcome")
            Text(text = user.name)
            Text(text = user.email)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        openSettings = {},
        logout = {},
        state = ProfileState(
            isLoading = false,
            user = UserEntity(
                id = "1",
                name = "Mahmoud ELDemerdash",
                email = "mahmoudeldemerdash5@gmail.com",
                imageUrl = null
            )
        ),
        onAction = {}
    )
}