package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridScopeMarker
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A lazy vertical grid layout. It composes only visible rows of the grid.
 *
 * Sample:
 * @sample androidx.compose.foundation.samples.LazyVerticalGridSample
 *
 * Sample with custom item spans:
 * @sample androidx.compose.foundation.samples.LazyVerticalGridSpanSample
 *
 * @param columns describes the count and the size of the grid's columns,
 * see [GridCells] doc for more information
 * @param modifier the modifier to apply to this layout
 * @param state the state object to be used to control or observe the list's state
 * @param contentPadding specify a padding around the whole content
 * @param reverseLayout reverse the direction of scrolling and layout. When `true`, items will be
 * laid out in the reverse order  and [LazyGridState.firstVisibleItemIndex] == 0 means
 * that grid is scrolled to the bottom. Note that [reverseLayout] does not change the behavior of
 * [verticalArrangement],
 * e.g. with [Arrangement.Top] (top) 123### (bottom) becomes (top) 321### (bottom).
 * @param verticalArrangement The vertical arrangement of the layout's children
 * @param horizontalArrangement The horizontal arrangement of the layout's children
 * @param flingBehavior logic describing fling behavior
 * @param userScrollEnabled whether the scrolling via the user gestures or accessibility actions
 * is allowed. You can still scroll programmatically using the state even when it is disabled.
 * @param content the [LazyGridScope] which describes the content
 */
@Composable
fun LazyVerticalGrid(
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: LazyGridScope.() -> Unit
){

}





/**
 * This class describes the count and the sizes of columns in vertical grids,
 * or rows in horizontal grids.
 */
@Stable
interface GridCells {
    /**
     * Calculates the number of cells and their cross axis size based on
     * [availableSize] and [spacing].
     *
     * For example, in vertical grids, [spacing] is passed from the grid's [Arrangement.Horizontal].
     * The [Arrangement.Horizontal] will also be used to arrange items in a row if the grid is wider
     * than the calculated sum of columns.
     *
     * Note that the calculated cross axis sizes will be considered in an RTL-aware manner --
     * if the grid is vertical and the layout direction is RTL, the first width in the returned
     * list will correspond to the rightmost column.
     *
     * @param availableSize available size on cross axis, e.g. width of [LazyVerticalGrid].
     * @param spacing cross axis spacing, e.g. horizontal spacing for [LazyVerticalGrid].
     * The spacing is passed from the corresponding [Arrangement] param of the lazy grid.
     */
    fun Density.calculateCrossAxisCellSizes(availableSize: Int, spacing: Int): List<Int>

    /**
     * Defines a grid with fixed number of rows or columns.
     *
     * For example, for the vertical [LazyVerticalGrid] Fixed(3) would mean that
     * there are 3 columns 1/3 of the parent width.
     */
    class Fixed(private val count: Int) : GridCells {
        init {
            require(count > 0)
        }

        override fun Density.calculateCrossAxisCellSizes(
            availableSize: Int,
            spacing: Int
        ): List<Int> {
            return calculateCellsCrossAxisSizeImpl(availableSize, count, spacing)
        }

        override fun hashCode(): Int {
            return -count // Different sign from Adaptive.
        }

        override fun equals(other: Any?): Boolean {
            return other is Fixed && count == other.count
        }
    }

    /**
     * Defines a grid with as many rows or columns as possible on the condition that
     * every cell has at least [minSize] space and all extra space distributed evenly.
     *
     * For example, for the vertical [LazyVerticalGrid] Adaptive(20.dp) would mean that
     * there will be as many columns as possible and every column will be at least 20.dp
     * and all the columns will have equal width. If the screen is 88.dp wide then
     * there will be 4 columns 22.dp each.
     */
    class Adaptive(private val minSize: Dp) : GridCells {
        init {
            require(minSize > 0.dp)
        }

        override fun Density.calculateCrossAxisCellSizes(
            availableSize: Int,
            spacing: Int
        ): List<Int> {
            val count = maxOf((availableSize + spacing) / (minSize.roundToPx() + spacing), 1)
            return calculateCellsCrossAxisSizeImpl(availableSize, count, spacing)
        }

        override fun hashCode(): Int {
            return minSize.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is Adaptive && minSize == other.minSize
        }
    }


}

private fun calculateCellsCrossAxisSizeImpl(
    gridSize: Int,
    slotCount: Int,
    spacing: Int
): List<Int> {
    val gridSizeWithoutSpacing = gridSize - spacing * (slotCount - 1)
    val slotSize = gridSizeWithoutSpacing / slotCount
    val remainingPixels = gridSizeWithoutSpacing % slotCount
    return List(slotCount) {
        slotSize + if (it < remainingPixels) 1 else 0
    }
}

/**
 * Receiver scope which is used by [LazyVerticalGrid].
 */
@LazyGridScopeMarker
sealed interface LazyGridScope {
    /**
     * Adds a single item to the scope.
     *
     * @param key a stable and unique key representing the item. Using the same key
     * for multiple items in the grid is not allowed. Type of the key should be saveable
     * via Bundle on Android. If null is passed the position in the grid will represent the key.
     * When you specify the key the scroll position will be maintained based on the key, which
     * means if you add/remove items before the current visible item the item with the given key
     * will be kept as the first visible one.
     * @param span the span of the item. Default is 1x1. It is good practice to leave it `null`
     * when this matches the intended behavior, as providing a custom implementation impacts
     * performance
     * @param contentType the type of the content of this item. The item compositions of the same
     * type could be reused more efficiently. Note that null is a valid type and items of such
     * type will be considered compatible.
     * @param content the content of the item
     */
    fun item(
        key: Any? = null,
        span: (LazyGridItemSpanScope.() -> GridItemSpan)? = null,
        contentType: Any? = null,
        content: @Composable LazyGridItemScope.() -> Unit
    )

    /**
     * Adds a [count] of items.
     *
     * @param count the items count
     * @param key a factory of stable and unique keys representing the item. Using the same key
     * for multiple items in the grid is not allowed. Type of the key should be saveable
     * via Bundle on Android. If null is passed the position in the grid will represent the key.
     * When you specify the key the scroll position will be maintained based on the key, which
     * means if you add/remove items before the current visible item the item with the given key
     * will be kept as the first visible one.
     * @param span define custom spans for the items. Default is 1x1. It is good practice to
     * leave it `null` when this matches the intended behavior, as providing a custom
     * implementation impacts performance
     * @param contentType a factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items
     * of such type will be considered compatible.
     * @param itemContent the content displayed by a single item
     */
    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        span: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
    )

    /**
     * Adds a sticky header item, which will remain pinned even when scrolling after it.
     * The header will remain pinned until the next header will take its place.
     *
     * @sample androidx.compose.foundation.samples.StickyHeaderSample
     *
     * @param key a stable and unique key representing the item. Using the same key
     * for multiple items in the list is not allowed. Type of the key should be saveable
     * via Bundle on Android. If null is passed the position in the list will represent the key.
     * When you specify the key the scroll position will be maintained based on the key, which
     * means if you add/remove items before the current visible item the item with the given key
     * will be kept as the first visible one.
     * @param contentType the type of the content of this item. The item compositions of the same
     * type could be reused more efficiently. Note that null is a valid type and items of such
     * type will be considered compatible.
     * @param content the content of the header
     */
    fun stickyHeader(
        key: Any? = null,
        span: (LazyGridItemSpanScope.() -> GridItemSpan) = { GridItemSpan(maxLineSpan.toLong()) },
        contentType: Any? = null,
        content: @Composable LazyGridItemScope.() -> Unit
    )
}