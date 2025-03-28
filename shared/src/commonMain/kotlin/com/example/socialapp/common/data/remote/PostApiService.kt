package com.example.socialapp.common.data.remote

import com.example.socialapp.account.data.model.ProfileApiResponse
import com.example.socialapp.common.data.model.LikeApiResponse
import com.example.socialapp.common.data.model.LikeParams
import com.example.socialapp.common.data.model.PostApiResponse
import com.example.socialapp.common.data.model.PostsApiResponse
import com.example.socialapp.common.util.Constants
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

internal class PostApiService: KtorApi() {
    suspend fun getFeedPosts(
        userToken: String,
        currentUserId: Long,
        page: Int,
        pageSize: Int
    ): PostsApiResponse {
        println("$page, $pageSize")
        val httpResponse = client.get{
            endpoint(path = "/posts/feed")
            parameter(key = Constants.CURRENT_USER_ID_PARAMETER, value = currentUserId)
            parameter(key = Constants.PAGE_QUERY_PARAMETER, value = page)
            parameter(key = Constants.PAGE_SIZE_QUERY_PARAMETER, value = pageSize)
            setToken(token = userToken)
        }

        return PostsApiResponse(code =  httpResponse.status, data = httpResponse.body())
    }

    suspend fun likePost(
        userToken: String,
        likeParams: LikeParams
    ): LikeApiResponse {
        val httpResponse = client.post{
            endpoint(path = "/post/likes/add")
            setBody(likeParams)
            setToken(token = userToken)
        }

        return LikeApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun unlikePost(
        userToken: String,
        likeParams: LikeParams
    ): LikeApiResponse {
        val httpResponse = client.delete(){
            endpoint(path = "/post/likes/remove")
            setBody(likeParams)
            setToken(token = userToken)
        }

        return LikeApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun getUserPosts(
        token: String,
        userId: Long,
        currentUserId: Long,
        page: Int,
        pageSize: Int
    ):PostsApiResponse {
        val httpResponse = client.get{
            endpoint("/posts/$userId")
            parameter(key = Constants.CURRENT_USER_ID_PARAMETER, value = currentUserId)
            parameter(key = Constants.PAGE_QUERY_PARAMETER, value = page)
            parameter(key = Constants.PAGE_SIZE_QUERY_PARAMETER, value = pageSize)
            setToken(token = token)
        }

        return PostsApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun getPost(
        token: String,
        postId: Long,
        currentUserId: Long
    ): PostApiResponse {
        val httpResponse = client.get{
            endpoint("/post/$postId")
            parameter(key = Constants.CURRENT_USER_ID_PARAMETER, value = currentUserId)
            setToken(token)
        }

        return PostApiResponse(code = httpResponse.status, data = httpResponse.body())
    }

    suspend fun createPost(
        token: String,
        postData: String,
        imageBytes: ByteArray
    ): PostApiResponse {
        val httpResponse = client.submitFormWithBinaryData(
            // post to include an image
            formData = formData {
                append(key = "post_data", value = postData)
                append(
                    key = "post_image",
                    value = imageBytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, value = "image/*")
                        append(HttpHeaders.ContentDisposition, value = "filename=post.jpg")
                    }
                )
            }
        ) {
            // configure http endpoint
            endpoint("/post/create")
            setToken(token = token)
            setupMultipartRequest()
            method = HttpMethod.Post
        }

        return PostApiResponse(code = httpResponse.status, data = httpResponse.body())
    }
}