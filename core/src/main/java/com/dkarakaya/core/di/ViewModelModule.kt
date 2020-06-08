package com.dkarakaya.core.di

import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
