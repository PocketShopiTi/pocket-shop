package com.iti.pocketshop.features.profile.presentation.components.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.R


@Composable
fun GuestAuthActions(openRegister: () -> Unit, openLogin: () -> Unit) {
    Button(
        onClick = openRegister,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
        )
        Text(
            text = stringResource(R.string.profile_create_account),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
    OutlinedButton(
        onClick = openLogin,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(44.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = stringResource(R.string.profile_log_in),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
