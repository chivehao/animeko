package me.him188.ani.datasources.api.paging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge

/**
 * A [Flow] that [totalSize] may be known in advance.
 */
interface SizedSource<out T> {
    /**
     * 全部搜索结果, 以 [Flow] 形式提供, 惰性请求.
     */
    val results: Flow<T>

    val finished: Flow<Boolean>

    /**
     * 总共的结果数量. 该数量不一定提供.
     */
    val totalSize: Flow<Int?>
}

/**
 * Merge multiple [SizedSource] into one.
 *
 * [Results][SizedSource.results] are be merged in the [Flow.merge] flavor.
 */
fun <T> Iterable<SizedSource<T>>.merge(): SizedSource<T> {
    return object : SizedSource<T> {
        override val results: Flow<T> = map { it.results }.merge()
        override val finished: Flow<Boolean> = combine(map { it.finished }) { values ->
            values.all { it }
        }

        override val totalSize: Flow<Int?> = combine(map { it.totalSize }) { values ->
            if (values.any { it == null }) {
                return@combine null
            }
            @Suppress("UNCHECKED_CAST")
            (values as Array<Int>).sum()
        }
    }
}


suspend inline fun SizedSource<*>.awaitFinished() {
    this.finished.filter { it }.first()
}