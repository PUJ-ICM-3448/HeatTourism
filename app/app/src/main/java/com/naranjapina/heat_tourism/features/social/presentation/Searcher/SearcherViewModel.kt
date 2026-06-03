package com.naranjapina.heat_tourism.features.social.presentation.Searcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import com.naranjapina.heat_tourism.features.social.data.repository.DestinationRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearcherViewModel(
    private val repository: DestinationRepository = DestinationRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow(SampleDestinations.ALL_CATEGORIES)
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _destinations = MutableStateFlow<List<MapPoint>>(emptyList())
    val destinations: StateFlow<List<MapPoint>> = _destinations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            combine(_query.debounce(300), _selectedCategory) { q, cat ->
                Pair(q, cat)
            }.collect { (q, cat) ->
                searchDestinations(q, cat)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    private fun searchDestinations(query: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _destinations.value = repository.getDestinationsByQuery(query, category)
            _isLoading.value = false
        }
    }
}
