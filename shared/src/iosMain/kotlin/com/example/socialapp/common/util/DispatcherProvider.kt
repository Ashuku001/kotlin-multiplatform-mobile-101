package com.example.socialapp.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class IOSDispatcher: DispatcherProvider{
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main

    override val io: CoroutineDispatcher
        get() = Dispatchers.Default
}

// the actual dispatcher for IOS
internal actual fun provideDispatcher(): DispatcherProvider = IOSDispatcher()