package com.draco.ktweak.retrofit

import com.google.gson.annotations.SerializedName

class GitHubCommits {
    @SerializedName("commit")
    val commit: GitHubCommit? = null

    @SerializedName("html_url")
    val url: String? = null
}

class GitHubBranch {
    @SerializedName("name")
    val name: String? = null
}

class GitHubCommit {
    @SerializedName("author")
    val author: GitHubCommitAuthor? = null

    @SerializedName("message")
    val message: String? = null
}

class GitHubCommitAuthor {
    @SerializedName("date")
    val date: String? = null
}