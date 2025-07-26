package com.bookmark.locker.data.model

import com.google.gson.annotations.SerializedName

data class GitHubRelease(
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("body")
    val body: String?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("prerelease")
    val prerelease: Boolean = false,
    @SerializedName("draft")
    val draft: Boolean = false
)
