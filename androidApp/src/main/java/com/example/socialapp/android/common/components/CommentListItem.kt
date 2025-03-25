package com.example.socialapp.android.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.socialapp.android.common.theming.DarkGray
import com.example.socialapp.android.common.theming.LargeSpacing
import com.example.socialapp.android.common.theming.LightGray
import com.example.socialapp.android.common.theming.MediumSpacing
import com.example.socialapp.android.R
import com.example.socialapp.android.common.fakedata.sampleComments
import com.example.socialapp.android.common.theming.SocialAppTheme
import com.example.socialapp.android.common.util.toCurrentUrl
import com.example.socialapp.post.domain.model.PostComment

@Composable
fun CommentListItem(
    modifier: Modifier = Modifier,
    comment: PostComment,
    onProfileClick: (Long) -> Unit,
    onMoreIconClick: (PostComment) -> Unit
) {
        Row (
            modifier = modifier.fillMaxWidth().padding(LargeSpacing),
            horizontalArrangement = Arrangement.spacedBy(MediumSpacing)
        ) {
            CircleImage(
                imageUrl = comment.userImageUrl?.toCurrentUrl(),
                modifier = modifier.size(30.dp)
            ) {
                onProfileClick(comment.userId)
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MediumSpacing),
                ){
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier.alignByBaseline()
                    )

                    Text(
                        text = comment.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSystemInDarkTheme()) {
                            DarkGray
                        } else {
                            LightGray
                        },
                        modifier = modifier.alignByBaseline().weight(1f) // take the entire space and push icon to the far right
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.round_more_horiz_24),
                        contentDescription = null,
                        tint = if (isSystemInDarkTheme()) {
                            DarkGray
                        } else {
                            LightGray
                        },
                        modifier = modifier.clickable { onMoreIconClick(comment) }
                    )
                }

                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }



}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CommentListItemPreview () {
    SocialAppTheme {
        Surface (color = MaterialTheme.colorScheme.surface) {
            CommentListItem(
                comment = sampleComments.first().toDomainPostComment(),
                onProfileClick = {},
                onMoreIconClick = {}
            )
        }
    }
}
