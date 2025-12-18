package com.kostas.common.logging

import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class TimberLogger @Inject constructor() : Logger {

    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).w(throwable, message)
        else Timber.tag(tag).w(message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).e(throwable, message)
        else Timber.tag(tag).e(message)
    }
}