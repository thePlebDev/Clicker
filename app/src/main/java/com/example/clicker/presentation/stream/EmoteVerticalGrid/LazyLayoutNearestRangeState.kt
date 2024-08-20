package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy

internal class LazyLayoutNearestRangeState(
    firstVisibleItem: Int,
    private val slidingWindowSize: Int,
    private val extraItemCount: Int
) : State<IntRange> {

    override var value: IntRange by mutableStateOf(
        calculateNearestItemsRange(firstVisibleItem, slidingWindowSize, extraItemCount),
        structuralEqualityPolicy()
    )
        private set

    private var lastFirstVisibleItem = firstVisibleItem

    fun update(firstVisibleItem: Int) {
        if (firstVisibleItem != lastFirstVisibleItem) {
            lastFirstVisibleItem = firstVisibleItem
            value = calculateNearestItemsRange(firstVisibleItem, slidingWindowSize, extraItemCount)
        }
    }

    private companion object {
        /**
         * Returns a range of indexes which contains at least [extraItemCount] items near
         * the first visible item. It is optimized to return the same range for small changes in the
         * firstVisibleItem value so we do not regenerate the map on each scroll.
         */
        private fun calculateNearestItemsRange(
            firstVisibleItem: Int,
            slidingWindowSize: Int,
            extraItemCount: Int
        ): IntRange {
            val slidingWindowStart = slidingWindowSize * (firstVisibleItem / slidingWindowSize)

            val start = maxOf(slidingWindowStart - extraItemCount, 0)
            val end = slidingWindowStart + slidingWindowSize + extraItemCount
            return start until end
        }
    }
}