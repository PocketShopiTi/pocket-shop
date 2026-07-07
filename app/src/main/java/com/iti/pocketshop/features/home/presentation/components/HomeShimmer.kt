package com.iti.pocketshop.features.home.presentation.components

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iti.pocketshop.core.components.shimmerEffect

@Composable
fun HomeShimmer(
    modifier: Modifier = Modifier,
    transition: InfiniteTransition = rememberInfiniteTransition()
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero banner shimmer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(MaterialTheme.shapes.large)
                    .shimmerEffect(transition)
            )
        }

        // Brands shimmer
        item {
            Spacer(Modifier.height(16.dp))
            SectionHeaderShimmer(transition)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .shimmerEffect(transition)
                    )
                }
            }
        }

        // Categories shimmer
        item {
            Spacer(Modifier.height(16.dp))
            SectionHeaderShimmer(transition)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect(transition)
                    )
                }
            }
        }

        // products shimmer
        item {
            Spacer(Modifier.height(20.dp))
            SectionHeaderShimmer(transition)
            Spacer(Modifier.height(12.dp))
            ProductRowShimmer(transition)
        }

    }
}

@Composable
private fun SectionHeaderShimmer(
    transition: InfiniteTransition
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(120.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect(transition)
        )
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(60.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect(transition)
        )
    }
}

@Composable
private fun ProductRowShimmer(
    transition: InfiniteTransition
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(3) {
            Column(
                modifier = Modifier.width(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(MaterialTheme.shapes.large)
                        .shimmerEffect(transition)
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.6f)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect(transition)
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect(transition)
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .width(60.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect(transition)
                )
            }
        }
    }
}
