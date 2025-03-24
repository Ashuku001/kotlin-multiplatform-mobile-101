package com.example.socialapp.android.common.util

import com.example.socialapp.common.util.Result
import kotlinx.coroutines.delay

// orchestrating the pagination logic and state change of view models
interface PagingManager<T> {
    suspend fun loadItems()
    fun reset()
}

class DefaultPagingManager<T> (
    private inline val onRequest: suspend (page: Int) -> Result<List<T>>,
    private inline val onSuccess: (items: List<T>, page: Int) -> Unit,
    private inline val onError: (cause: String, page: Int) -> Unit,
    private inline val onLoadStateChange: (isLoading: Boolean) -> Unit
): PagingManager<T> {
    private var currentPage = Constants.INITIAL_PAGE_NUMBER
    private var isLoading = false

    override suspend fun loadItems() {
        if(isLoading) return
        isLoading = true
        // inform the viewmodel that the load state has changed
        onLoadStateChange(true)
        delay(3000)

        val result = onRequest(currentPage)
        isLoading = false
        onLoadStateChange(false)

        when(result){
            is Result.Error -> {
                onError(result.message ?: Constants.UNEXPECTED_ERROR_MESSAGE, currentPage)
            }
            is Result.Success -> {
                onSuccess(result.data!!, currentPage)
                currentPage += 1 // increment to the next page
            }
        }
    }

    override  fun reset() {
        // reset to initial page
        currentPage = Constants.INITIAL_PAGE_NUMBER
    }
}