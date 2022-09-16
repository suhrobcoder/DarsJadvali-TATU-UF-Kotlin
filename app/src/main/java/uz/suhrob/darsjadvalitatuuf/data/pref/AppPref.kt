package uz.suhrob.darsjadvalitatuuf.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPref(
    private val context: Context,
) {

    val groupFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[GROUP_KEY]
        }

    suspend fun saveGroupName(name: String) {
        context.dataStore.edit { settings ->
            settings[GROUP_KEY] = name
        }
    }

    companion object {
        private val GROUP_KEY = stringPreferencesKey("group")
    }
}