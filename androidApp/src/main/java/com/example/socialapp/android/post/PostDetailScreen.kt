package com.example.socialapp.android.post

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialapp.android.R
import com.example.socialapp.android.common.components.CircleImage
import com.example.socialapp.android.common.components.CommentListItem
import com.example.socialapp.android.common.components.PostListItem
import com.example.socialapp.android.common.components.ScreenLevelLoadingErrorView
import com.example.socialapp.android.common.components.ScreenLevelLoadingView
import com.example.socialapp.android.common.components.loadingMoreItems
import com.example.socialapp.android.common.fakedata.sampleComments
import com.example.socialapp.android.common.fakedata.samplePosts
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.common.theming.SmallSpacing
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.post.domain.model.PostComment
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen (
    modifier: Modifier = Modifier,
    postUiState: PostUiState,
    commentsUiState: CommentsUiState,
    onProfileNavigation: (userId: Long) -> Unit,
    postId: Long,
    onUiAction: (PostDetailUiAction) -> Unit
) {

    val listState = rememberLazyListState()

    val shouldFetchMoreComments by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()

                (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount)
            }
        }
    }

    var commentText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current // to hide keyboard

    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope() // calling new coroutine
    val showBottomSheet by remember { mutableStateOf(false) }
    var selectedComment by rememberSaveable(stateSaver = postCommentSaver) {
        mutableStateOf(null)
    }

    println("selected comment ${sheetState.isVisible} $selectedComment")

    if (selectedComment != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedComment = null }, // Dismiss when clicked outside
            sheetState = sheetState,
            content = {
                selectedComment?.let { postComment ->
                    CommentMoreActionsBottomSheetContent(
                        comment = postComment,
                        canDeleteComment = postComment.userId == postUiState.post?.userId || postComment.isOwner,
                        onDeleteCommentClick = {comment ->
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if(sheetState.isVisible) {
                                    onUiAction(PostDetailUiAction.RemoveCommentAction(comment))
                                    selectedComment = null
                                }
                            }
                        },
                        onNavigateToProfile = {userId ->
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if(!sheetState.isVisible) {
                                    selectedComment = null
                                    onProfileNavigation(userId)
                                }
                            }
                        }
                    )
                }
            }
        )
    }

    if (postUiState.isLoading) {
        ScreenLevelLoadingView()
    } else if (postUiState.post != null) {
        Column (
            modifier = modifier
        ) {
            LazyColumn (
                modifier = modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .weight(1f),
                state = listState
            ) {
                item(key = "post_item") {
                    PostListItem(
                        post = postUiState.post,
                        onPostClick = {},
                        onProfileClick =  {},
                        onLikeClick = { onUiAction(PostDetailUiAction.LikeOrUnlikePostAction(it))},
                        onCommentClick = { TODO() },
                        isDetailScreen = true
                    )
                }

                item(key = "comments_header_section") {
                    CommentSectionHeader()
                }

                if(commentsUiState.isLoading || commentsUiState.isAddingNewComment) {
                    loadingMoreItems()
                }

                items(
                    items = commentsUiState.comments,
                    key = {comment ->"${comment.commentId}_${Random.nextInt()}" }
                ) {
                    HorizontalDivider()
                    CommentListItem(
                        comment = it,
                        onProfileClick = onProfileNavigation,
                        onMoreIconClick = {
                            selectedComment = it
                            scope.launch { sheetState.show() }
                        }
                    )
                }
            }

            CommentInput(
                commentText = commentText,
                onCommentChange = { commentText = it },
                onSendClick = {
                    keyboardController?.hide() // hide the keyboard
                    onUiAction(PostDetailUiAction.AddPostCommentAction(it))
                    commentText = ""
                }
            )
        }

    } else {
        ScreenLevelLoadingErrorView {
            onUiAction(PostDetailUiAction.FetchPostAction(postId = postId))
        }
    }

    LaunchedEffect(
        key1 = Unit,
        block = { onUiAction(PostDetailUiAction.FetchPostAction(postId = postId)) }
    )

    LaunchedEffect( key1 = shouldFetchMoreComments) {
        println("$shouldFetchMoreComments,${!commentsUiState.endReached}")
        if(shouldFetchMoreComments && !commentsUiState.endReached) {
            println("to fetch more $shouldFetchMoreComments")
            onUiAction(PostDetailUiAction.LoadMoreComments)
        }
    }

}

@Composable
fun CommentSectionHeader(
    modifier: Modifier = Modifier,
) {
    Row  (
        modifier = modifier
            .fillMaxWidth()
            .padding(LargeSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = R.string.comments_label),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun CommentInput(
    modifier: Modifier = Modifier,
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendClick: (String) -> Unit
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .animateContentSize()
    ) {
        HorizontalDivider()
        Row(
            modifier = modifier.padding(
                horizontal = LargeSpacing,
                vertical = MediumSpacing
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LargeSpacing)
        ) {
            Box(
                modifier = modifier
                    .heightIn(min = 35.dp, max = 70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(percent = 25)
                    )
                    .padding(
                        horizontal = MediumSpacing,
                        vertical = SmallSpacing
                    )
                    .weight(1f)
            ){
                BasicTextField(
                    value = commentText,
                    onValueChange = onCommentChange,
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart),

                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                    cursorBrush = SolidColor(LocalContentColor.current)
                ) {
                    // a hint
                    if(commentText.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = SmallSpacing),
                            text = stringResource(id = R.string.comment_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
            SendCommentButton(
                sendCommentEnabled = commentText.isNotBlank(),
                onSendClick = {
                    onSendClick(commentText)
                }
            )
        }
    }
}

@Composable
private fun CommentMoreActionsBottomSheetContent(
    modifier: Modifier = Modifier,
    comment: PostComment,
    canDeleteComment: Boolean,
    onDeleteCommentClick: (comment: PostComment) -> Unit,
    onNavigateToProfile: (userId: Long) -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.comment_more_actions_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.padding(all = MediumSpacing)
        )

        HorizontalDivider()

        // delete comment
        ListItem(
            modifier = modifier.clickable(
                enabled = canDeleteComment,
                onClick = { onDeleteCommentClick(comment) }
            ),
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.delete_comment_action),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        )

        println("THE COMMENT $comment")

        // user that made the comment
        ListItem(
            modifier = modifier.clickable { onNavigateToProfile(comment.userId) },
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.view_profile_action) + " ${comment.userName}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                CircleImage (
                    imageUrl = comment.userImageUrl,
                    onClick = {}
                )
            }
        )
    }
}



@Composable
private fun SendCommentButton(
    modifier: Modifier = Modifier,
    sendCommentEnabled: Boolean,
    onSendClick: () -> Unit
){
    val border = if (!sendCommentEnabled) {
        BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    } else {
        null
    }

    Button(
        modifier = modifier.height(35.dp),
        enabled = sendCommentEnabled,
        onClick = onSendClick,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        ),
        border = border,
        shape = RoundedCornerShape(percent = 50),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            stringResource(id = R.string.send_button_text),
            modifier = modifier.padding(horizontal = 16.dp)
        )
    }

}

// create a custom saver to persist comments accross configuration changes to manage selected comment from the list
private val postCommentSaver = Saver<PostComment?, Any>(
    save = { postComment ->
        if (postComment != null) { // convert post comment to a map
            mapOf(
                "commentId" to postComment.commentId,
                "content" to postComment.content,
                "postId" to postComment.postId,
                "userId" to postComment.userId,
                "userName" to postComment.userName,
                "userImageUrl" to postComment.userImageUrl,
                "createdAt" to postComment.createdAt
            )
        } else {
            null
        }
    },
    restore = { savedValue -> // reconstruct commen the saved comments
        val map = savedValue as Map<*, *>
        PostComment(
            commentId = map["commentId"] as Long,
            content = map["content"] as String,
            postId = map["postId"] as Long,
            userId = map["userId"] as Long,
            userName = map["userName"] as String,
            userImageUrl = map["userImageUrl"] as String?,
            createdAt = map["createdAt"] as String
        )
    }
)



@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PostDetailPreview() {
    SocialAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PostDetailScreen(
                postUiState = PostUiState(
                    isLoading = false,
                    post = samplePosts.first().toDomainPost()
                ),
                commentsUiState = CommentsUiState(
                    isLoading = false,
                    comments = sampleComments.map { it.toDomainPostComment() }
                ),
                onProfileNavigation = { },
                postId = 1,
                onUiAction = {}
            )
        }
    }
}