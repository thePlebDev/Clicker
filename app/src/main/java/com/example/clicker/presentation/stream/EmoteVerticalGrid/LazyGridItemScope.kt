package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.grid.LazyGridScopeMarker
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

/**
 * Receiver scope being used by the item content parameter of [LazyVerticalGrid].
 */
@Stable
@LazyGridScopeMarker
sealed interface LazyGridItemScope {

    /**
     * Have the content fill the [Constraints.maxWidth] and [Constraints.maxHeight] of the parent
     * measurement constraints by setting the [minimum width][Constraints.minWidth] to be equal to
     * the [maximum width][Constraints.maxWidth] multiplied by [fraction] and the [minimum
     * height][Constraints.minHeight] to be equal to the [maximum height][Constraints.maxHeight]
     * multiplied by [fraction]. Note that, by default, the [fraction] is 1, so the modifier will
     * make the content fill the whole available space. [fraction] must be between `0` and `1`.
     *
     * Regular [Modifier.fillMaxSize] can't work inside the scrolling layouts as the items are
     * measured with [Constraints.Infinity] as the constraints for the main axis.
     */
    fun Modifier.fillParentMaxSize(
        /*@FloatRange(from = 0.0, to = 1.0)*/
        fraction: Float = 1f
    ): Modifier

    /**
     * Have the content fill the [Constraints.maxWidth] of the parent measurement constraints
     * by setting the [minimum width][Constraints.minWidth] to be equal to the
     * [maximum width][Constraints.maxWidth] multiplied by [fraction]. Note that, by default, the
     * [fraction] is 1, so the modifier will make the content fill the whole parent width.
     * [fraction] must be between `0` and `1`.
     *
     * Regular [Modifier.fillMaxWidth] can't work inside the scrolling horizontally layouts as the
     * items are measured with [Constraints.Infinity] as the constraints for the main axis.
     */
    fun Modifier.fillParentMaxWidth(
        /*@FloatRange(from = 0.0, to = 1.0)*/
        fraction: Float = 1f
    ): Modifier

    /**
     * Have the content fill the [Constraints.maxHeight] of the incoming measurement constraints
     * by setting the [minimum height][Constraints.minHeight] to be equal to the
     * [maximum height][Constraints.maxHeight] multiplied by [fraction]. Note that, by default, the
     * [fraction] is 1, so the modifier will make the content fill the whole parent height.
     * [fraction] must be between `0` and `1`.
     *
     * Regular [Modifier.fillMaxHeight] can't work inside the scrolling vertically layouts as the
     * items are measured with [Constraints.Infinity] as the constraints for the main axis.
     */
    fun Modifier.fillParentMaxHeight(
        /*@FloatRange(from = 0.0, to = 1.0)*/
        fraction: Float = 1f
    ): Modifier

    /**
     * This modifier animates the item placement within the Lazy grid.
     *
     * When you provide a key via [LazyGridScope.item]/[LazyGridScope.items] this modifier will
     * enable item reordering animations. Aside from item reordering all other position changes
     * caused by events like arrangement or alignment changes will also be animated.
     *
     * @param animationSpec a finite animation that will be used to animate the item placement.
     */
    fun Modifier.animateItemPlacement(
        animationSpec: FiniteAnimationSpec<IntOffset> = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ): Modifier
}