package com.graphhopper.android

import android.os.AsyncTask

abstract class GHAsyncTask<A, B, C> : AsyncTask<A, B, C>() {
    var error: Throwable? = null
        private set

    val errorMessage: String?
        get() = if (hasError()) {
            error!!.message
        } else "No Error"

    @Throws(Exception::class)
    protected abstract fun saveDoInBackground(vararg params: A): C

    override fun doInBackground(vararg params: A): C? {
        try {
            return saveDoInBackground(*params)
        } catch (t: Throwable) {
            error = t
            return null
        }

    }

    fun hasError(): Boolean {
        return error != null
    }
}
