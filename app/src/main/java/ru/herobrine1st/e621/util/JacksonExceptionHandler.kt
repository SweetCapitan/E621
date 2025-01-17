/*
 * This file is part of ru.herobrine1st.e621.
 *
 * ru.herobrine1st.e621 is an android client for https://e621.net
 * Copyright (C) 2022-2023 HeroBrine1st Erquilenne <project-e621-android@herobrine1st.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.herobrine1st.e621.util

import android.util.Log
import androidx.compose.material.SnackbarDuration
import com.fasterxml.jackson.core.JacksonException
import ru.herobrine1st.e621.R
import ru.herobrine1st.e621.ui.snackbar.SnackbarAdapter
import javax.inject.Inject
import javax.inject.Singleton

// This class will send deserialization errors to developers (either anonymously or with user consent, idk)
// Right now it does nothing but logs
// Should it handle any IOException?
@Singleton
class JacksonExceptionHandler @Inject constructor(
    private val snackbarAdapter: SnackbarAdapter
) {
    suspend fun handleDeserializationError(exception: JacksonException) {
        Log.e(TAG, "An exception occurred while deserializing response", exception)
        snackbarAdapter.enqueueMessage(R.string.jackson_deserialization_error, SnackbarDuration.Indefinite)
    }

    companion object {
        const val TAG = "JacksonExceptionHandler"
    }
}