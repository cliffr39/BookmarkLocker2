package com.bookmark.locker.network

import com.bookmark.locker.data.model.GitHubRelease
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface GitHubApiService {
    
    @Headers("Accept: application/vnd.github.v3+json")
    @GET("repos/cliffr39/BookmarkLocker2/releases/latest")
    suspend fun getLatestRelease(): Response<GitHubRelease>
}
