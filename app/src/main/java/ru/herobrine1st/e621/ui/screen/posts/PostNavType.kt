package ru.herobrine1st.e621.ui.screen.posts

import android.os.Bundle
import androidx.navigation.NavType
import com.fasterxml.jackson.module.kotlin.readValue
import ru.herobrine1st.e621.api.model.Post
import ru.herobrine1st.e621.util.objectMapper

class PostNavType : NavType<Post>(false) {
    override fun get(bundle: Bundle, key: String): Post? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Post {
        return objectMapper.readValue(value)
    }

    override fun put(bundle: Bundle, key: String, value: Post) {
        bundle.putParcelable(key, value)
    }
}