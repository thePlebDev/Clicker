package com.example.clicker.presentation.minigames.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.clicker.R
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold


@Composable
fun MiniGameViews(
    onNavigate: (Int) -> Unit,
){
    MiniGameScaffold(
        onNavigate={destination -> onNavigate(destination)}
    )
}

@Composable
fun MiniGameScaffold(
    onNavigate: (Int) -> Unit,
){
    NoDrawerScaffold(
        topBar = {
            TopBarTextRow("Mini games")
        },
        bottomBar = {
            this.FourButtonNavigationBottomBarRow(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                horizontalArrangement = Arrangement.SpaceAround,
                firstButton = {
                    IconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Home",
                        imageVector = Icons.Default.Home,
                        iconContentDescription = "Stay on home page",
                        onClick = {
                            //DONE
                            onNavigate(R.id.action_miniGameFragment_to_homeFragment)
                        },
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                secondButton = {
                    PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Mod Channels",
                        painter = painterResource(R.drawable.moderator_white),
                        iconContentDescription = "Navigate to mod channel page",
                        onClick = {
                            onNavigate(R.id.action_miniGameFragment_to_modChannelsFragment)
                                  },
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                thirdButton = {

                    this.PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        iconContentDescription = "Navigate to search bar",
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Categories",
                        onClick = {

                            onNavigate(R.id.action_miniGameFragment_to_searchFragment)
                        },
                    )
                },

                fourthButton = {
                    this.PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.secondary,
                        painter = painterResource(id = R.drawable.videogame_asset),
                        iconContentDescription = "Navigate to mini games page",
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Mini Games",
                        onClick = {

                        },
                    )

                }

            )

        },
        content = { contentPadding ->

        },
    )
}