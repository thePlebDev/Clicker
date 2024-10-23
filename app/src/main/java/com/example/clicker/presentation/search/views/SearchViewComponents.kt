package com.example.clicker.presentation.search.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.views.LoginLogoutScaffoldDrawer
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.search.views.mainComponents.CategoryModal
import com.example.clicker.presentation.search.views.mainComponents.SearchBarUI
import com.example.clicker.presentation.search.views.mainComponents.SearchViewComponent
import com.example.clicker.presentation.sharedViews.DrawerScaffold
import com.example.clicker.presentation.sharedViews.LogoutDialog
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchView(
    onNavigate: (Int) -> Unit,
    homeViewModel:HomeViewModel,
    searchViewModel: SearchViewModel,
    hapticFeedBackError:() ->Unit,

){
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""

    // This is where the modal should be

    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden,skipHalfExpanded =true)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetBackgroundColor= MaterialTheme.colorScheme.primary,
        sheetState = state,
        sheetContent = {
            CategoryModal()
        }
    ) {

        SearchMainComponent(
            onNavigate={action -> onNavigate(action)},
            topGamesListResponse = searchViewModel.topGames.value,

            searchRefreshing = searchViewModel.searchRefreshing.value,
            searchRefreshFunc ={searchViewModel.pullToRefreshTopGames()},
            showNetworkMessage=searchViewModel.searchNetworkStatus.value.showMessage,
            topGamesList=searchViewModel.topGamesList.toList(),
            hapticFeedBackError={hapticFeedBackError()},
            categoryDoubleClickedAdd={id->searchViewModel.doubleClickedCategoryAdd(id)},
            categoryDoubleClickedRemove ={topGame->searchViewModel.doubleClickedCategoryRemove(topGame)},
            pinnedList = searchViewModel.topGamesPinnedList.toList(),
            pinned = searchViewModel.pinnedFilter.value,
            changePinnedListFilterStatus={searchViewModel.updatePinnedFilter()},
            fetchMoreTopGames={
                searchViewModel.fetchMoreTopGames()
            },
            openCategoryModal={
                scope.launch {
                    state.show()
                }
            }

        )

    }



}

@Composable
fun SearchMainComponent(
    onNavigate: (Int) -> Unit,
    topGamesListResponse: Response<Boolean>,
    searchRefreshing:Boolean,
    searchRefreshFunc:()->Unit,
    showNetworkMessage:Boolean,
    hapticFeedBackError:() ->Unit,
    topGamesList: List<TopGame>,
    categoryDoubleClickedAdd:(String)->Unit,
    categoryDoubleClickedRemove:(TopGame)->Unit,
    pinned:Boolean,
    pinnedList:List<TopGame>,
    changePinnedListFilterStatus:()->Unit,
    fetchMoreTopGames:()->Unit,
    openCategoryModal:()->Unit,


){
    val scope = rememberCoroutineScope()


    NoDrawerScaffold(
        topBar = {

            SearchBarUI(
                changePinnedListFilterStatus={changePinnedListFilterStatus()},
                pinned=pinned
            )

        },
        bottomBar = {
            TripleButtonNavigationBottomBarRow(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                horizontalArrangement= Arrangement.SpaceAround,
                firstButton = {
                    IconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Home",
                        imageVector = Icons.Default.Home,
                        iconContentDescription = "Navigate to home page",
                        onClick ={onNavigate(R.id.action_searchFragment_to_homeFragment)},
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                secondButton = {
                    PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Mod Channels",
                        painter = painterResource(R.drawable.moderator_white),
                        iconContentDescription = "Navigate to mod channel page",
                        onClick ={onNavigate(R.id.action_searchFragment_to_modChannelsFragment)},
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                thirdButton = {
                    IconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.secondary,
                        text = "Search",
                        imageVector = Icons.Default.Search,
                        iconContentDescription = "Stay on search page",
                        onClick = {onNavigate(R.id.action_homeFragment_to_searchFragment)},
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
            )
        },
    ) {contentPadding ->
        PullToRefreshComponent(
            padding = contentPadding,
            refreshing = searchRefreshing,
            refreshFunc = {searchRefreshFunc()},
            showNetworkMessage=showNetworkMessage,
            networkStatus = {modifier ->


            }
        ) {
            //THIS IS WHERE THE MODAL SHOULD GO
            SearchViewComponent(
                topGamesListResponse = topGamesListResponse,
                showNetworkRefreshError = showNetworkMessage,
                hapticFeedBackError={hapticFeedBackError()},
                topGamesList=topGamesList,
                categoryDoubleClickedAdd={id -> categoryDoubleClickedAdd(id)},
                categoryDoubleClickedRemove ={topGame -> categoryDoubleClickedRemove(topGame)},
                pinned = pinned,
                pinnedList = pinnedList,
                fetchMoreTopGames={fetchMoreTopGames()},
                openCategoryModal={openCategoryModal()}

            )
        }
    }
}

