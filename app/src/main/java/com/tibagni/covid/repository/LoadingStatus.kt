package com.tibagni.covid.repository

data class LoadingStatus(val status: Status, val message: String?) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun success() = LoadingStatus(Status.SUCCESS, null)
        fun loading() = LoadingStatus(Status.LOADING, null)
        fun fail(message: String? = null) = LoadingStatus(Status.ERROR, message)
    }
}