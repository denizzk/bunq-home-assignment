package com.dkarakaya.request.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import com.dkarakaya.request.RequestViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface RequestViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RequestViewModel::class)
    fun bindRequestViewModel(requestViewModel: RequestViewModel): ViewModel

    @Module
    companion object {
        @Provides
        fun provideRequestViewModel(): RequestViewModel {
            return RequestViewModel(
                createApiContext = CreateApiContext(),
                apiContextFilePath = ApiContextFilePath()
            )
        }
    }
}
