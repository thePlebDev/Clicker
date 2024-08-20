package com.example.clicker.presentation.stream.EmoteVerticalGrid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutIntervalContent
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
internal class LazyGridIntervalContent(
    content: LazyGridScope.() -> Unit
) : LazyGridScope, LazyLayoutIntervalContent<LazyGridInterval>() {
    internal val spanLayoutProvider: LazyGridSpanLayoutProvider = LazyGridSpanLayoutProvider(this)

    override val intervals = MutableIntervalList<LazyGridInterval>()

    private var _headerIndexes: MutableList<Int>? = null
    val headerIndexes: List<Int> get() = _headerIndexes ?: emptyList()

    internal var hasCustomSpans = false

    init {
        apply(content)
    }

    override fun item(
        key: Any?,
        span: (LazyGridItemSpanScope.() -> GridItemSpan)?,
        contentType: Any?,
        content: @Composable LazyGridItemScope.() -> Unit
    ) {
        intervals.addInterval(
            1,
            LazyGridInterval(
                key = key?.let { { key } },
                span = span?.let { { span() } } ?: DefaultSpan,
                type = { contentType },
                item = { content() }
            )
        )
        if (span != null) hasCustomSpans = true
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        span: (LazyGridItemSpanScope.(Int) -> GridItemSpan)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
    ) {
        intervals.addInterval(
            count,
            LazyGridInterval(
                key = key,
                span = span ?: DefaultSpan,
                type = contentType,
                item = itemContent
            )
        )
        if (span != null) hasCustomSpans = true
    }

    override fun stickyHeader(
        key: Any?,
        span: LazyGridItemSpanScope.() -> GridItemSpan,
        contentType: Any?,
        content: @Composable LazyGridItemScope.() -> Unit
    ) {
        val headersIndexes = _headerIndexes ?: mutableListOf<Int>().also { _headerIndexes = it }
        headersIndexes.add(intervals.size)

        item(key = key, span = span, contentType = contentType, content = content)
    }

    private companion object {
        val DefaultSpan: LazyGridItemSpanScope.(Int) -> GridItemSpan = { GridItemSpan(1) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal class LazyGridInterval(
    override val key: ((index: Int) -> Any)?,
    val span: LazyGridItemSpanScope.(Int) -> GridItemSpan,
    override val type: ((index: Int) -> Any?),
    val item: @Composable LazyGridItemScope.(Int) -> Unit
) : LazyLayoutIntervalContent.Interval