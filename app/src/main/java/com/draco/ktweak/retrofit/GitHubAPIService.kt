package com.draco.ktweak.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubAPIService {
    @GET("commits?")
    fun getCommits(
        @Query("sha")
        branch: String
    ): Call<List<GitHubCommits>>

    @GET("branches")
    fun getBranches(): Call<List<GitHubBranch>>
}