package com.example.utilities

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.utilityStore: DataStore<Preferences> by preferencesDataStore(name = "utility_tools")

/** Recently used (last 6) and favourite tools, kept in DataStore. Old ids are mapped to merged tools. */
class UtilityPrefs(context: Context) {
    private val store = context.applicationContext.utilityStore

    val recents: Flow<List<String>> = store.data.map { prefs ->
        prefs[RECENTS].orEmpty().split(",").mapNotNull { ToolRegistry.resolve(it)?.id }.distinct()
    }

    val favorites: Flow<Set<String>> = store.data.map { prefs ->
        prefs[FAVORITES].orEmpty().mapNotNull { ToolRegistry.resolve(it)?.id }.toSet()
    }

    suspend fun recordUse(id: String) {
        store.edit { prefs ->
            val current = prefs[RECENTS].orEmpty().split(",").filter { it.isNotBlank() }
            prefs[RECENTS] = (listOf(id) + current).distinct().take(MAX_RECENTS).joinToString(",")
        }
    }

    suspend fun toggleFavorite(id: String) {
        store.edit { prefs ->
            val current = prefs[FAVORITES].orEmpty()
            prefs[FAVORITES] = if (id in current) current - id else current + id
        }
    }

    companion object {
        const val MAX_RECENTS = 6
        private val RECENTS = stringPreferencesKey("recent_tools")
        private val FAVORITES = stringSetPreferencesKey("favorite_tools")
    }
}

data class UtilitiesState(
    val query: String = "",
    /** Null means the "All" tab. */
    val category: ToolCategory? = null,
    val recents: List<UtilityTool> = emptyList(),
    val favorites: Set<String> = emptySet()
) {
    val visibleTools: List<UtilityTool>
        get() = ToolRegistry.search(query).filter { query.isNotBlank() || category == null || it.category == category }
}

class UtilitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UtilityPrefs(application)
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<ToolCategory?>(null)

    val state: StateFlow<UtilitiesState> = combine(query, category, prefs.recents, prefs.favorites) { q, c, recents, favorites ->
        UtilitiesState(q, c, recents.mapNotNull(ToolRegistry::resolve), favorites)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UtilitiesState())

    fun setQuery(value: String) { query.value = value }

    fun setCategory(value: ToolCategory?) { category.value = value }

    fun opened(id: String) { viewModelScope.launch { prefs.recordUse(id) } }

    fun toggleFavorite(id: String) { viewModelScope.launch { prefs.toggleFavorite(id) } }
}
