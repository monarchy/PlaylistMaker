package com.example.playlistmaker

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    isLoading: Boolean,
    isError: Boolean,
    isEmpty: Boolean,
    filteredTracks: List<Track>,
    onRetry: () -> Unit,
    onBackPressed: () -> Unit,


) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(

                title = { Text(text = stringResource(R.string.search), color = colorResource(R.color.placeholder_text)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.backgroundColor))
            )
        },
        bottomBar = {
            BottomAppBar(
                horizontalArrangement = BottomAppBarDefaults.HorizontalArrangement,
                containerColor = colorResource(R.color.backgroundColor),
                content = {
                    BottomBarItem(
                        icon = painterResource(id = R.drawable.search_button),
                        label = stringResource(R.string.search),
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    BottomBarItem(
                        icon =  painterResource(id = R.drawable.mediateka),
                        label = stringResource(R.string.media),
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    BottomBarItem(
                        icon =  painterResource(id = R.drawable.settings),
                        label = stringResource(R.string.settings),
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                })
        }

    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.backgroundColor))
            ) {
                SearchBar(
                    searchText = searchText,
                    onSearchTextChanged = onSearchTextChanged,
                    onSearch = onSearch,
                    onCleanPressed = {
                        onSearchTextChanged("")
                        onSearch("")
                    }
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                if (isError) {
                    ErrorPlaceholder(onRetry = onRetry)
                } else if (isEmpty) {
                    EmptyPlaceholder()
                } else {
                    if (filteredTracks.isNotEmpty()) {
                        TrackList(tracks = filteredTracks)
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCleanPressed:() -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_button),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onSearch(searchText) }
                ),
                singleLine = true

            )
            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = { onCleanPressed() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "Clear",
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.size(16.dp))
        }
        }
    }
}

@Composable
fun ErrorPlaceholder(onRetry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_error),
            contentDescription = "Error",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.placeholder_error),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Обновить")
        }
    }
}


@Composable
fun EmptyPlaceholder() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_nothing_found),
            contentDescription = "No Results",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.placeholder_no_results), style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun TrackList(tracks: List<Track>) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(track = track)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            model = track.artworkUrl100,
            contentDescription = "Обложка",
            modifier = Modifier
                .size(40.dp)
                .clip(RectangleShape)
                .background(Color.Gray)
                .clip(RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp)),
            contentScale = ContentScale.Crop

        )


        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formatDuration(track.trackTime),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}


@SuppressLint("DefaultLocale")
fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}
@Preview
@Composable
fun DefaultPreview() {
        SearchScreen(
            searchText = "",
            onSearchTextChanged = {},
            onSearch = {},
            isLoading = false,
            isError = false,
            isEmpty = false,
            filteredTracks = emptyList(),
            onRetry = {},
            onBackPressed = {}
        )
    }

@Composable
fun BottomBarItem(
    icon: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected) Color.Blue else colorResource(R.color.placeholder_text),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (selected) Color.Blue else colorResource(R.color.placeholder_text),
        )
    }
}

