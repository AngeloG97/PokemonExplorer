package com.example.pokeapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import com.example.pokeapp.R
import com.example.pokeapp.ui.PokemonViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

val funFont = FontFamily(Font(R.font.fredoka))

@Composable
fun PokemonView(viewModel: PokemonViewModel, modifier: Modifier = Modifier) {

    val focusManager = LocalFocusManager.current
    val selectedType by viewModel.selectedType.collectAsState()
    val pokemonList by viewModel.pokemonList.collectAsState()
    val selectedPokemon by viewModel.selectedPokemon.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val displayedCount by viewModel.displayedCount.collectAsState()
    val hasLoadedMore by viewModel.hasLoadedMore.collectAsState()

    val displayedList = viewModel.getDisplayedList(pokemonList, searchQuery, displayedCount)
    val hasMore = displayedList.size >= displayedCount && displayedList.size < pokemonList.size

    BackHandler(enabled = selectedType.isNotEmpty() || selectedPokemon != null) {
        viewModel.goBack()
    }

    Box(modifier = modifier.fillMaxSize()) {

        // main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {

            // logo
            Image(
                painter = painterResource(id = R.drawable.pokemon_explorer_logo),
                contentDescription = "Pokémon Explorer Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // type grid
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val buttonWidth = screenWidth * 0.28f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    Row {
                        viewModel.types.take(9).forEach { type ->
                            Button(
                                onClick = { viewModel.selectType(type) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(viewModel.typeColors[type] ?: 0xFF000000)
                                ),
                                border = if (selectedType == type) BorderStroke(2.dp, Color.White)
                                         else null,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .width(buttonWidth)
                            ) {
                                Text(
                                    type.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    Row {
                        viewModel.types.drop(9).forEach { type ->
                            Button(
                                onClick = { viewModel.selectType(type) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(viewModel.typeColors[type] ?: 0xFF000000)
                                ),
                                border = if (selectedType == type) BorderStroke(2.dp, Color.White)
                                         else null,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .width(buttonWidth)
                            ) {
                                Text(
                                    type.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // search bar
            if (selectedType.isNotEmpty()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.onSearchQueryChange(it)
                    },
                    label = { Text("Search Pokémon") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(viewModel.typeColors[selectedType] ?: 0xFF6200EE),
                        unfocusedBorderColor = Color(viewModel.typeColors[selectedType] ?: 0xFF6200EE),
                        cursorColor = Color(viewModel.typeColors[selectedType] ?: 0xFF6200EE),
                        focusedLabelColor = Color(viewModel.typeColors[selectedType] ?: 0xFF6200EE)
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                modifier = Modifier.clickable {
                                    viewModel.onSearchQueryChange("")
                                },
                                tint = Color.White
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // welcome message
            if (selectedType.isEmpty() && !isLoading) {
                val infiniteTransition = rememberInfiniteTransition(label = "bounce")
                val offsetY by infiniteTransition.animateFloat(
                    initialValue = -5f,
                    targetValue = 5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bounce"
                )

                Text(
                    text = "Tap a type and catch 'em all!",
                    fontSize = 20.sp,
                    color = Color(0xFFF7D02C),
                    fontFamily = funFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                        .offset(y = offsetY.dp)
                )
            }

            // error
            if (error != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { viewModel.retry() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
                    ) {
                        Text(
                            "Retry",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // loading
            if (isLoading) {
                val infiniteTransition = rememberInfiniteTransition(label = "spin")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing)
                    ),
                    label = "spin"
                )

                Image(
                    painter = painterResource(id = R.drawable.pokeball_loading),
                    contentDescription = "Loading",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                        .graphicsLayer { rotationZ = rotation }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // pokemon list
            Box(modifier = Modifier.weight(1f)) {

                // no results message
                if (selectedType.isNotEmpty() && searchQuery.isNotBlank() && displayedList.isEmpty() && !isLoading) {
                    Text(
                        text = "No Pokémon found matching \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedList) { pokemonInfo ->
                        val pokemonId = pokemonInfo.url.trimEnd('/').substringAfterLast('/')
                        val spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.selectPokemon(pokemonInfo.name)
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = spriteUrl,
                                contentDescription = pokemonInfo.name,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pokemonInfo.name.uppercase(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    if (hasMore) {
                        item {
                            if (hasLoadedMore) {
                                LaunchedEffect(displayedCount) {
                                    kotlinx.coroutines.delay(500)
                                    viewModel.loadMore()
                                }
                                val infiniteTransition = rememberInfiniteTransition(label = "listSpin")
                                val rotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000, easing = LinearEasing)
                                    ),
                                    label = "listSpin"
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.pokeball_loading),
                                    contentDescription = "Loading more",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .size(36.dp)
                                        .graphicsLayer { rotationZ = rotation }
                                )
                            } else {
                                Button(
                                    onClick = { viewModel.loadMore() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(viewModel.typeColors[selectedType] ?: 0xFFCC0000)
                                    )
                                ) {
                                    Text(
                                        "Load More",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // pokemon details overlay
        if (selectedPokemon != null) {
            val pokemon = selectedPokemon!!
            val typeColor = Color(viewModel.typeColors[selectedType] ?: 0xFF6200EE)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        viewModel.clearSelectedPokemon()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2A2A2A))
                        .padding(16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (pokemon.sprites.frontDefault != null) {
                        AsyncImage(
                            model = pokemon.sprites.frontDefault,
                            contentDescription = pokemon.name,
                            modifier = Modifier.size(200.dp)
                        )
                    } else {
                        Text(
                            text = "No image available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Text(
                        text = pokemon.name.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val hp = pokemon.stats.find { it.stat.name == "hp" }?.baseStat
                    val attack = pokemon.stats.find { it.stat.name == "attack" }?.baseStat
                    val defense = pokemon.stats.find { it.stat.name == "defense" }?.baseStat
                    val specialAttack = pokemon.stats.find { it.stat.name == "special-attack" }?.baseStat
                    val specialDefense = pokemon.stats.find { it.stat.name == "special-defense" }?.baseStat
                    val speed = pokemon.stats.find { it.stat.name == "speed" }?.baseStat

                    StatBar("HP", hp, typeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("Attack", attack, typeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("Defense", defense, typeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("Sp.Attack", specialAttack, typeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("Sp.Defense", specialDefense, typeColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("Speed", speed, typeColor)
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@Composable
fun StatBar(label: String, value: Int?, color: Color) {
    var triggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        triggered = true
    }

    val animatedFraction by animateFloatAsState(
        targetValue = if (triggered) (value ?: 0) / 255f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "stat"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(75.dp),
            fontSize = 14.sp
        )
        Text(
            text = "${value ?: "?"}",
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
            fontSize = 14.sp
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Gray.copy(alpha = 0.3f))

        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedFraction)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}
