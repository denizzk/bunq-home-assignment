package com.dkarakaya.payment.di

import com.dkarakaya.payment.PaymentActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface PaymentModule {

    @ContributesAndroidInjector(
        modules = [
            PaymentViewModelModule::class
        ]
    )
    fun contributePaymentActivity(): PaymentActivity
}
