# Pokémon Explorer - v1.0

An Android app that lets users browse Pokémon by type, search by name, and view detailed stats — built with Kotlin, Jetpack Compose, and the PokéAPI.

## Features

- **Type Selection:** 10 Pokémon types displayed as a scrollable color-coded grid with visual selection feedback.
- **Pokémon List:** Displays Pokémon with sprites, starting with 10 results. A "Load More" button reveals additional Pokémon, followed by infinite scroll.
- **Search:** Filter Pokémon by name within the selected type, with a clear button for quick resets.
- **Pokémon Details:** Tap any Pokémon to view its image, name, and base stats (HP, Attack, Defense) in an overlay modal with animated stat bars.
- **Error Handling:** Network and loading errors display user-friendly messages with a Retry button. Missing images and stats are handled gracefully.
- **Back Navigation:** The system back button steps through app states (close details → clear type → exit).
- **Caching:** In-memory caching avoids redundant API calls when switching between previously loaded types.

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** pattern:

```
data/
├── model/          → Data classes mirroring the PokéAPI JSON structure
├── network/        → Retrofit API service and client configuration
└── repository/     → Single data source with in-memory caching

ui/
├── PokemonViewModel.kt  → Manages UI state and user interactions
├── screens/
│   └── PokemonView.kt   → Jetpack Compose UI
└── theme/                → Dark theme configuration
```

**Data flow:** View observes ViewModel state via StateFlow → ViewModel calls Repository → Repository calls API (or returns cached data) → ViewModel updates state → View redraws automatically.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM with StateFlow
- **Networking:** Retrofit 2 + Gson converter
- **Image Loading:** Coil
- **Async:** Kotlin Coroutines with viewModelScope
- **API:** PokéAPI (https://pokeapi.co/)

## API & Pagination Note

The PokéAPI `/type/{name}` endpoint returns all Pokémon belonging to a type in a single response — it does not support server-side pagination parameters (offset/limit). Implementing server-side filtered pagination would require scanning the entire Pokémon database and filtering by type client-side, resulting in significantly more API calls with no benefit.

Instead, the app fetches the complete list of names in one efficient call and implements **client-side pagination**: displaying 10 results initially, with a Load More button followed by infinite scroll. Individual Pokémon details (image, stats) are fetched from the API on demand — only when the user selects a specific Pokémon.

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on an emulator or physical device (min SDK 26)

The APK is available in the [Releases](https://github.com/AngeloG97/PokemonExplorer/releases) section.

## Dependencies

```
Retrofit 2.11.0
Gson Converter 2.11.0
Coil Compose 2.7.0
Lifecycle ViewModel Compose 2.8.7
Lifecycle ViewModel KTX 2.8.7
```

# Pokémon Explorer - v2.0

Now allows the user to choose among all 18 Pokémon types, and displays more Pokémon stats: Sp.Attack, Sp.Defense and Speed are included.
