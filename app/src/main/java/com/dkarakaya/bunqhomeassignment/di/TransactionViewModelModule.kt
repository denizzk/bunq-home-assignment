package com.dkarakaya.bunqhomeassignment.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.bunqhomeassignment.transaction.TransactionViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface TransactionViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    fun bindTransactionViewModel(transactionViewModel: TransactionViewModel): ViewModel

    @Module
    companion object {
        @Provides
        fun provideTransactionViewModel(): TransactionViewModel {
            return TransactionViewModel(
                createApiContext = CreateApiContext(),
                apiContextFilePath = ApiContextFilePath()
            )
        }
    }
}
