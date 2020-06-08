package com.dkarakaya.bunqhomeassignment.di

import com.dkarakaya.bunqhomeassignment.BaseApplication
import com.dkarakaya.payment.di.PaymentModule
import com.dkarakaya.request.di.RequestModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        MainModule::class,
        PaymentModule::class,
        RequestModule::class
    ]
)
@Singleton
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: BaseApplication): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(instance: BaseApplication?) = Unit
}
