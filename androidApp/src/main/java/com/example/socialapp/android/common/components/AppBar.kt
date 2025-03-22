package com.example.socialapp.android.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.socialapp.android.R
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.socialapp.android.common.theming.SmallElevation
import com.example.socialapp.android.destinations.EditProfileDestination
import com.example.socialapp.android.destinations.FollowersDestination
import com.example.socialapp.android.destinations.FollowingDestination
import com.example.socialapp.android.destinations.HomeDestination
import com.example.socialapp.android.destinations.PostDetailDestination
import com.example.socialapp.android.destinations.ProfileDestination
import com.example.socialapp.android.destinations.loginDestination
import com.example.socialapp.android.destinations.signupDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val  currentDestination = navHostController.currentDestinationAsState().value
    Surface(
        modifier = modifier,
        tonalElevation = SmallElevation
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = getAppBarTitle(currentDestination?.route))
                )
            },
            modifier = modifier,
//            colors = MaterialTheme.colorScheme.surface,
            actions = {
                AnimatedVisibility(visible =  currentDestination?.route == HomeDestination.route) {
                    Icon (
                        painter = painterResource(id = R.drawable.person_circle_icon),
                        contentDescription = null
                    )
                }
            },
            navigationIcon = if (shouldShowNavigationIcon(currentDestination?.route)) {
                {
                    IconButton (
                        onClick = {navHostController.navigateUp()}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            } else {
                { null }
            }
        )
    }
}

private fun getAppBarTitle(currentDestinationRoute: String?): Int {
    return when(currentDestinationRoute){
        loginDestination.route -> R.string.login_destination_title
        signupDestination.route -> R.string.signup_destination_title
        HomeDestination.route -> R.string.home_destination_title
        PostDetailDestination.route -> R.string.post_detail_destination_title
        ProfileDestination.route -> R.string.profile_destination_title
        EditProfileDestination.route -> R.string.edit_profile_destination_title
        FollowingDestination.route -> R.string.following_text
        FollowersDestination.route -> R.string.followers_text
        else -> R.string.no_destination_title
    }
}

private fun shouldShowNavigationIcon(currentDestinationRoute: String?): Boolean {
    return !(currentDestinationRoute == loginDestination.route || currentDestinationRoute == signupDestination.route || currentDestinationRoute == HomeDestination.route )
}