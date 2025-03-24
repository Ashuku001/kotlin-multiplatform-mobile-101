package com.example.socialapp.android.common.util

import com.example.socialapp.common.domain.model.Post
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = Constants.EVENT_BUS_BUFFER_CAPACITY)
    val events = _events.asSharedFlow()

    suspend fun send(event: Event) {
        _events.emit(event)
    }
}

sealed interface Event{
    data class PostUpdated(val post: Post):Event
}