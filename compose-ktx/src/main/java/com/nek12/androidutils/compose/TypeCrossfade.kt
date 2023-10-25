package com.nek12.androidutils.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Any> TypeCrossfade(
    state: T,
    modifier: Modifier = Modifier,
    fill: Boolean = true,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable context(BoxScope) T.() -> Unit
) {
    val transition = updateTransition(targetState = state, label = "TypeCrossfade")
    transition.Crossfade(
        contentKey = { it::class },
        animationSpec = animationSpec,
        modifier = modifier.animateContentSize(),
    ) {
        Box(
            modifier = Modifier.then(if (fill) Modifier.fillMaxSize() else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            content(it)
        }
    }
}
