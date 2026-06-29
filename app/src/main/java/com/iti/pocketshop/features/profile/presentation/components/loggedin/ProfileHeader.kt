package com.iti.pocketshop.features.profile.presentation.components.loggedin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iti.pocketshop.R
import com.iti.pocketshop.features.profile.domain.model.UserEntity
import com.iti.pocketshop.ui.theme.FrauncesFontFamily
import com.iti.pocketshop.ui.theme.PlusJakartaSansFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileHeader(
    user: UserEntity,
    modifier: Modifier = Modifier,
) {
    val displayName = user.name.ifBlank { stringResource(R.string.profile_default_name) }
    val initials = remember(displayName) {
        displayName
            .trim()
            .split(Regex("\\s+"))
            .filter(String::isNotBlank)
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
    }
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (user.imageUrl.isNullOrBlank()) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FrauncesFontFamily,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                AsyncImage(
                    model = user.imageUrl,
                    contentDescription = stringResource(R.string.profile_user_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = stringResource(R.string.profile_welcome_back),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = PlusJakartaSansFontFamily,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FrauncesFontFamily,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
            user.memberSinceEpochMillis?.let { timestamp ->
                val memberSince = remember(timestamp) {
                    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(timestamp))
                }
                Text(
                    text = stringResource(R.string.profile_member_since, memberSince),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = PlusJakartaSansFontFamily,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
