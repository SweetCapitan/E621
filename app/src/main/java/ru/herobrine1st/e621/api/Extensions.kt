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

package ru.herobrine1st.e621.api

import android.util.Log
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import ru.herobrine1st.e621.util.objectMapper

private suspend fun <T> Call<T>.awaitResponseInternal(): Response<T> {
    val response = this.awaitResponse()
    if (response.code() !in 200..399) { // Include redirects
        val message = kotlin.run {
            if(response.code() == 404) return@run "Not found"
            val body = withContext(Dispatchers.IO) {
                objectMapper.readValue<ObjectNode>(response.errorBody()!!.charStream())
            }
            body.get("message")?.asText() ?: body.toPrettyString()
        }

        Log.e("API", "Got unsuccessful response: ${response.code()} ${response.message()}")
        Log.e("API", message)
        throw ApiException(message, response.code())
    }
    return response
}

suspend fun <T> Call<T>.awaitResponse(): Response<T> = this.awaitResponseInternal()
suspend fun <T> Call<T>.await(): T {
    val response = this.awaitResponseInternal()
    val body = response.body()
    // 204 is "No Content" meaning body length is 0 bytes and is not "Null Response" :///
    if (response.code() == 204 && body == null) {
        Log.e(
            "API",
            "Response code is 204, therefore body is null, use awaitResponse() instead"
        )
    }
    return response.body()!!
}