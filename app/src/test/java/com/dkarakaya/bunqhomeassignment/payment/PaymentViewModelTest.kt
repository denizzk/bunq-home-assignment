package com.dkarakaya.bunqhomeassignment.payment

import com.bunq.sdk.model.generated.endpoint.Payment
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import org.junit.Test

class PaymentViewModelTest {

    private val createApiContext = CreateApiContext()
    private val apiContextFilePath = ApiContextFilePath()
    private val viewModel by lazy {
        com.dkarakaya.payment.PaymentViewModel(
            createApiContext,
            apiContextFilePath
        )
    }

    @Test
    fun `GIVEN payment details WHEN make payment THEN return payment id`() {

        val getPaymentId = viewModel.getPayment().test()

        getPaymentId
            .assertValue(Payment())
            .assertNoErrors()
            .assertNotComplete()
    }

//    givenApiContext(): ApiContext(){
//        return ApiContext.
//    }
}
