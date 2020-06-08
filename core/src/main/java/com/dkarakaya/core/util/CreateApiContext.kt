package com.dkarakaya.core.util

import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class CreateApiContext {

    operator fun invoke(filePath: String): @NonNull Single<ApiContext> {
        return Single.fromCallable {
            if (File(filePath).exists()) {
                ApiContext.restore(filePath)
            } else {
                ApiContext.create(
                    ApiEnvironmentType.SANDBOX,
                    API_KEY,
                    DEVICE_DESCRIPTION
                )
            }
        }
            .subscribeOn(Schedulers.io())
    }

    companion object {
        const val API_KEY = "sandbox_eb5be4b95dbbc4b779dac31afa3d0d7e656a68a8b1d28cc9fbff5645"
        const val DEVICE_DESCRIPTION = "emulator"
    }
}
