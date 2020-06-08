package com.dkarakaya.bunqhomeassignment.transaction

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.bunq.sdk.model.generated.endpoint.Payment

class TransactionController : EpoxyController() {

    var transactions: List<Payment> = listOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    var transactionClickListener: (transaction: Payment) -> Unit = { }

    override fun buildModels() {
        transactions.forEachIndexed { id, transaction ->
            transaction {
                id(id)
                transaction(transaction)
                onClickListener(View.OnClickListener {
                    transactionClickListener(transaction)
                })
            }
        }
    }
}
