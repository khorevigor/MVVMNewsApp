package com.androiddevs.mvvmnewsapp.utils

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            javaClass.simpleName
        } else {
            javaClass.name
        }
    }