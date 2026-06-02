package com.naranjapina.heat_tourism.features.social.presentation.Post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.social.data.model.Comment
import com.naranjapina.heat_tourism.features.social.data.model.Post
import com.naranjapina.heat_tourism.features.social.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val repository = SocialRepository()

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var activePostId: String? = null

    fun loadPostAndComments(postId: String) {
        if (activePostId == postId) return
        activePostId = postId
        _isLoading.value = true

        viewModelScope.launch {
            // Escuchar la publicación en tiempo real
            launch {
                repository.getPostFlow(postId)
                    .catch { e -> e.printStackTrace() }
                    .collect { updatedPost ->
                        _post.value = updatedPost
                        _isLoading.value = false
                    }
            }
// ... código existente ...

            // Escuchar comentarios en tiempo real
            launch {
                repository.getCommentsFlow(postId)
                    .catch { e -> e.printStackTrace() }
                    .collect { updatedComments ->
                        _comments.value = updatedComments
                    }
            }
        }
    }

    fun addComment(text: String, userId: String, userName: String, userAvatar: String? = null) {
        val postId = activePostId ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                val comment = Comment(
                    userId = userId,
                    userName = userName,
                    userAvatar = userAvatar,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                repository.addComment(postId, comment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleLike(userId: String) {
        val postId = activePostId ?: return
        viewModelScope.launch {
            try {
                repository.toggleLike(postId, userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
