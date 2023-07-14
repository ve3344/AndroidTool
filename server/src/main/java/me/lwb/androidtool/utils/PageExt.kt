package me.lwb.androidtool.utils

/**
 * Created by ve3344 .
 */
fun <T> List<T>.page(pageIndex: Int, pageSize: Int): List<T> {
    if (pageSize <= 0 || isEmpty()) {
        return emptyList()
    }
    val start = pageSize * (pageIndex.coerceAtLeast(1) - 1)
    val end = (start + pageSize).coerceAtMost(size)
    if (start >= end) {
        return emptyList()
    }
    return subList(start, end)
}

