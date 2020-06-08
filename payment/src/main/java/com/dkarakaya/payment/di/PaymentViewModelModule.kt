package com.dkarakaya.payment.di

import androidx.lifecycle.ViewModel
import com.dkarakaya.core.di.ViewModelKey
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import com.dkarakaya.payment.PaymentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
interface PaymentViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    fun bindPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel

    @Module
    companion object {
        @Provides
        fun providePaymentViewModel(): PaymentViewModel {
            return PaymentViewModel(
                createApiContext = CreateApiContext(),
                apiContextFilePath = ApiContextFilePath()
            )
        }
    }
}
