package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.LazyLayoutPinnableItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@OptIn(ExperimentalFoundationApi::class)
internal interface LazyGridItemProvider : LazyLayoutItemProvider {
    val keyIndexMap: LazyLayoutKeyIndexMap
    val spanLayoutProvider: LazyGridSpanLayoutProvider

    /** The list of indexes of the sticky header items */
    val headerIndexes: List<Int>
}

@Composable
internal fun rememberLazyGridItemProviderLambda(
    state: LazyGridState,
    content: LazyGridScope.() -> Unit,
): () -> LazyGridItemProvider {
    val latestContent = rememberUpdatedState(content)
    return remember(state) {
        val intervalContentState = derivedStateOf(referentialEqualityPolicy()) {
            LazyGridIntervalContent(latestContent.value)
        }
        val itemProviderState = derivedStateOf(referentialEqualityPolicy()) {
            val intervalContent = intervalContentState.value
            val map = NearestRangeKeyIndexMap(state.nearestRange, intervalContent)
            LazyGridItemProviderImpl(
                state = state,
                intervalContent = intervalContent,
                keyIndexMap = map
            )
        }
        itemProviderState::value
    }
}

private class LazyGridItemProviderImpl(
    private val state: LazyGridState,
    private val intervalContent: LazyGridIntervalContent,
    override val keyIndexMap: LazyLayoutKeyIndexMap,
) : LazyGridItemProvider {

    override val itemCount: Int get() = intervalContent.itemCount
    override val headerIndexes: List<Int> get() = intervalContent.headerIndexes

    override fun getKey(index: Int): Any = keyIndexMap.getKey(index) ?: intervalContent.getKey(index)

    override fun getContentType(index: Int): Any? = intervalContent.getContentType(index)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Item(index: Int, key: Any) {
        LazyLayoutPinnableItem(key, index, state.pinnedItems) {
            intervalContent.withInterval(index) { localIndex, content ->
                content.item(LazyGridItemScopeImpl, localIndex)
            }
        }
    }

    override val spanLayoutProvider: LazyGridSpanLayoutProvider
        get() = intervalContent.spanLayoutProvider

    override fun getIndex(key: Any): Int = keyIndexMap.getIndex(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyGridItemProviderImpl) return false

        // the identity of this class is represented by intervalContent object.
        // having equals() allows us to skip items recomposition when intervalContent didn't change
        return intervalContent == other.intervalContent
    }

    override fun hashCode(): Int {
        return intervalContent.hashCode()
    }
}