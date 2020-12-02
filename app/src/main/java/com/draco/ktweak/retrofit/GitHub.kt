package com.draco.ktweak.retrofit

import android.content.Context
import com.draco.ktweak.R
import com.draco.ktweak.utils.Commit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHub(context: Context) {
    private val gitAuthor = context.getString(R.string.git_author)
    private val gitRepo = context.getString(R.string.git_repo)
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/repos/$gitAuthor/$gitRepo/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(GitHubAPIService::class.java)

    fun commits(branch: String, callback: (List<Commit>) -> Unit) {
        val call = service.getCommits(branch)
        val commits = arrayListOf<Commit>()
        call.enqueue(object : Callback<List<GitHubCommits>> {
            override fun onResponse(
                call: Call<List<GitHubCommits>>?,
                response: Response<List<GitHubCommits>>?
            ) {
                val gitHubAPIResponses = response?.body()!!

                for (commitResponse in gitHubAPIResponses) {
                    val commit = Commit()
                    with(commit) {
                        try {
                            message = commitResponse.commit!!.message!!.lines()[0]
                            date = commitResponse.commit.author!!.date!!
                                .replace("T", "\n")
                                .replace("Z", "")
                            url = commitResponse.url!!
                            commits += commit
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                callback(commits)
            }

            override fun onFailure(call: Call<List<GitHubCommits>>?, t: Throwable?) {}
        })
    }

    fun branches(callback: (List<String>) -> Unit) {
        val call = service.getBranches()
        val branches = arrayListOf<String>()
        call.enqueue(object : Callback<List<GitHubBranch>> {
            override fun onResponse(
                call: Call<List<GitHubBranch>>?,
                response: Response<List<GitHubBranch>>?
            ) {
                val gitHubAPIResponses = response?.body()!!

                for (branchResponse in gitHubAPIResponses) {
                    branches += branchResponse.name!!
                }

                callback(branches)
            }

            override fun onFailure(call: Call<List<GitHubBranch>>?, t: Throwable?) {}
        })
    }
}