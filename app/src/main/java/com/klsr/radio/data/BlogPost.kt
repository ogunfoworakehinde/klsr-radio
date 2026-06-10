package com.klsr.radio.data

data class BlogPost(
    val id: Int,
    val title: String,
    val excerpt: String,
    val date: String,
    val featuredMediaUrl: String?,
    val authorName: String
)
