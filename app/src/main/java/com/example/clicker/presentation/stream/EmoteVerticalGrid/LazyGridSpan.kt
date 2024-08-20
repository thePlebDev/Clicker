package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.foundation.lazy.grid.LazyGridScopeMarker
import androidx.compose.runtime.Immutable

/**
 * Scope of lambdas used to calculate the spans of items in lazy grids.
 */
@LazyGridScopeMarker
sealed interface LazyGridItemSpanScope {
    /**
     * The max current line (horizontal for vertical grids) the item can occupy, such that
     * it will be positioned on the current line.
     *
     * For example if [LazyVerticalGrid] has 3 columns this value will be 3 for the first cell in
     * the line, 2 for the second cell, and 1 for the last one. If you return a span count larger
     * than [maxCurrentLineSpan] this means we can't fit this cell into the current line, so the
     * cell will be positioned on the next line.
     */
    val maxCurrentLineSpan: Int

    /**
     * The max line span (horizontal for vertical grids) an item can occupy. This will be the
     * number of columns in vertical grids or the number of rows in horizontal grids.
     *
     * For example if [LazyVerticalGrid] has 3 columns this value will be 3 for each cell.
     */
    val maxLineSpan: Int
}

/**
 * Represents the span of an item in a [LazyVerticalGrid].
 */
@Immutable
@JvmInline
value class GridItemSpan internal constructor(private val packedValue: Long) {
    /**
     * The span of the item on the current line. This will be the horizontal span for items of
     * [LazyVerticalGrid].
     */
    val currentLineSpan: Int get() = packedValue.toInt()
}