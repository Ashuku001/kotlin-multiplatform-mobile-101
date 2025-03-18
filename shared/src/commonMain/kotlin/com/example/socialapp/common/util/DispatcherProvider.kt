package com.example.socialapp.common.util

import kotlinx.coroutines.CoroutineDispatcher

internal interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}

// and expect function to provide dispatcher (should come from android and ios)
internal expect fun provideDispatcher(): DispatcherProvider