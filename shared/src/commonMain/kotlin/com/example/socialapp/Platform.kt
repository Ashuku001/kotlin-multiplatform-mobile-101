package com.example.socialapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform