package com.dkarakaya.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_transaction_details.*

class TransactionDetailsBottomSheetDialog : BottomSheetDialogFragment() {

    private var transaction: Int? = null
    private var transactionAmount: String? = null
    private var transactionRecipient: String? = null
    private var transactionType: String? = null
    private var transactionDate: String? = null
    private var transactionDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transaction = it.getInt(ARG_TRANSACTION)
            transactionAmount = it.getString(ARG_TRANSACTION_AMOUNT)
            transactionRecipient = it.getString(ARG_TRANSACTION_RECIPIENT)
            transactionType = it.getString(ARG_TRANSACTION_TYPE)
            transactionDate = it.getString(ARG_TRANSACTION_DATE)
            transactionDescription = it.getString(ARG_TRANSACTION_DESCRIPTION)
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        textSectionHeader.text = transaction?.let { getString(it) }
        textAmount.text = transactionAmount
        textRecipient.text = transactionRecipient
        textType.text = transactionType
        textDate.text = transactionDate
        textDescription.text = transactionDescription
    }

    companion object {
        private const val ARG_TRANSACTION = "transaction"
        private const val ARG_TRANSACTION_AMOUNT = "transaction_amount"
        private const val ARG_TRANSACTION_RECIPIENT = "transaction_recipient"
        private const val ARG_TRANSACTION_TYPE = "transaction_type"
        private const val ARG_TRANSACTION_DATE = "transaction_date"
        private const val ARG_TRANSACTION_DESCRIPTION = "transaction_description"

        /**
         * @param transaction Transaction e.g. PAYMENT or REQUEST.
         * @param amount Transaction amount.
         * @param recipient Transaction recipient.
         * @param type Transaction type.
         * @param date Transaction date.
         * @param description Transaction description.
         * @return A new instance of fragment TransactionDetailsBottomSheetDialog.
         */
        fun newInstance(
            transaction: Int,
            amount: String,
            recipient: String,
            type: String,
            date: String,
            description: String
        ) =
            TransactionDetailsBottomSheetDialog()
                .apply {
                    arguments = Bundle().apply {
                        putInt(ARG_TRANSACTION, transaction)
                        putString(ARG_TRANSACTION_AMOUNT, amount)
                        putString(ARG_TRANSACTION_RECIPIENT, recipient)
                        putString(ARG_TRANSACTION_TYPE, type)
                        putString(ARG_TRANSACTION_DATE, date)
                        putString(ARG_TRANSACTION_DESCRIPTION, description)
                    }
                }
    }
}
