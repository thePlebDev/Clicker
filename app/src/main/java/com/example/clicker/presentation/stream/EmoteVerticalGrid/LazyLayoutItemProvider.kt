package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider

/**
 * Finds a position of the item with the given key in the lists. This logic allows us to
 * detect when there were items added or removed before our current first item.
 */
@ExperimentalFoundationApi
internal fun LazyLayoutItemProvider.findIndexByKey(
    key: Any?,
    lastKnownIndex: Int,
): Int {
    if (key == null || itemCount == 0) {
        // there were no real item during the previous measure
        return lastKnownIndex
    }
    if (lastKnownIndex < itemCount &&
        key == getKey(lastKnownIndex)
    ) {
        // this item is still at the same index
        return lastKnownIndex
    }
    val newIndex = getIndex(key)
    if (newIndex != -1) {
        return newIndex
    }
    // fallback to the previous index if we don't know the new index of the item
    return lastKnownIndex
}