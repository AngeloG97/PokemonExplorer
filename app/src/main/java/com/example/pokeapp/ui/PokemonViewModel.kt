package com.example.pokeapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapp.data.model.Pokemon
import com.example.pokeapp.data.model.PokemonRef
import com.example.pokeapp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


class PokemonViewModel : ViewModel() {

    private val repository = PokemonRepository()

    val types = listOf(
        "normal", "fire", "water", "electric", "grass", "ice", "fighting",
        "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
        "dragon", "dark", "steel", "fairy"
    )

    val typeColors = mapOf(
        "normal" to 0xFFA8A77A,
        "fire" to 0xFFEE8130,
        "water" to 0xFF6390F0,
        "electric" to 0xFFF7D02C,
        "grass" to 0xFF7AC74C,
        "ice" to 0xFF96D9D6,
        "fighting" to 0xFFC22E28,
        "poison" to 0xFFA33EA1,
        "ground" to 0xFFE2BF65,
        "flying" to 0xFFA98FF3,
        "psychic" to 0xFFF95587,
        "bug" to 0xFFA6B91A,
        "rock" to 0xFFB6A136,
        "ghost" to 0xFF735797,
        "dragon" to 0xFF6F35FC,
        "dark" to 0xFF705746,
        "steel" to 0xFFB7B7CE,
        "fairy" to 0xFFD685AD
    )

    // UI-state

    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType

    private val _pokemonList = MutableStateFlow<List<PokemonRef>>(emptyList())
    val pokemonList: StateFlow<List<PokemonRef>> = _pokemonList

    private val _selectedPokemon = MutableStateFlow<Pokemon?>(null)
    val selectedPokemon: StateFlow<Pokemon?> = _selectedPokemon

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var _lastSelectedPokemonName: String? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _displayedCount = MutableStateFlow(10)
    val displayedCount: StateFlow<Int> = _displayedCount

    private val _hasLoadedMore = MutableStateFlow(false)
    val hasLoadedMore: StateFlow<Boolean> = _hasLoadedMore


    // user interaction

    fun selectType(type: String) {
        _selectedType.value = type
        _selectedPokemon.value = null
        _error.value = null
        _searchQuery.value = ""
        _displayedCount.value = 10
        _hasLoadedMore.value = false
        loadPokemonList(type)
    }


    private fun loadPokemonList(type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = repository.getPokemonsByType(type)
                _pokemonList.value = list
            } catch (e: IOException) {
                _error.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                _error.value = "Something went wrong. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun selectPokemon(name: String) {
        _lastSelectedPokemonName = name
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val pokemon = repository.getPokemon(name)
                _selectedPokemon.value = pokemon
            } catch (e: IOException) {
                _error.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                _error.value = "Could not load ${name}. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearSelectedPokemon() {
        _selectedPokemon.value = null
    }


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }


    fun retry() {
        if (_lastSelectedPokemonName != null && _selectedPokemon.value == null) {
            selectPokemon(_lastSelectedPokemonName!!)
        } else if (_selectedType.value.isNotEmpty()) {
            selectType(_selectedType.value)
        }
    }


    fun getDisplayedList(
        allPokemon: List<PokemonRef>,
        query: String,
        count: Int
    ): List<PokemonRef> {
        val filtered = if (query.isBlank()) {
            allPokemon
        } else {
            allPokemon.filter { it.name.startsWith(query, ignoreCase = true) }
        }
        return filtered.take(count)
    }


    fun loadMore() {
        _displayedCount.value += 10
        _hasLoadedMore.value = true
    }


    fun goBack(): Boolean {
        if (_selectedPokemon.value != null) {
            _selectedPokemon.value = null
            return true
        }
        if (_selectedType.value.isNotEmpty()) {
            _selectedType.value = ""
            _pokemonList.value = emptyList()
            _selectedPokemon.value = null
            _searchQuery.value = ""
            _displayedCount.value = 10
            _error.value = null
            return true
        }
        return false
    }
}