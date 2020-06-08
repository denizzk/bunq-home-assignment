package com.dkarakaya.bunqhomeassignment.di

import com.dkarakaya.bunqhomeassignment.transaction.TransactionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainModule {

    @ContributesAndroidInjector(
        modules = [
            TransactionViewModelModule::class
        ]
    )
    fun contributeMainActivity(): TransactionActivity
}
