package com.example.socialapp.common.util

import kotlinx.coroutines.CoroutineDispatcher

internal interface DispatcherProvider {
    val io: CoroutineDispatcher // for network calls,
    val main: CoroutineDispatcher // updating the ui
}

// and expect function to provide dispatcher (should come from android and ios)
// Expect platform specific dispatchers
internal expect fun provideDispatcher(): DispatcherProvider