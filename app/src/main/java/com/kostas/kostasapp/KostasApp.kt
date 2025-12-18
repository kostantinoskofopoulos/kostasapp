package com.kostas.kostasapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class KostasApp : Application(), ImageLoaderFactory {

    @Inject lateinit var imageLoader: ImageLoader

    override fun newImageLoader(): ImageLoader = imageLoader

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}