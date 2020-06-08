package com.dkarakaya.request.di

import com.dkarakaya.request.RequestActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface RequestModule {

    @ContributesAndroidInjector(
        modules = [
            RequestViewModelModule::class
        ]
    )
    fun contributeRequestActivity(): RequestActivity
}
