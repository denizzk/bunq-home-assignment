package com.dkarakaya.request

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dkarakaya.core.TransactionDetailsBottomSheetDialog
import com.dkarakaya.core.util.CreateNotification
import com.dkarakaya.core.util.dateFormatter
import com.dkarakaya.core.viewmodel.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_request.*
import timber.log.Timber
import javax.inject.Inject

class RequestActivity : DaggerAppCompatActivity(R.layout.activity_request) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RequestViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(RequestViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerListeners()
        registerSubscriptions()
    }

    private fun registerListeners() {
        lateinit var paymentType: String

        // type buttons are not used for the payment only for ui
        buttonEmail.setOnClickListener {
            paymentType = "email"
            buttonIban.isEnabled = false
            buttonPhone.isEnabled = false
            setVisibilityRecipientSection(R.string.sugar_daddy_email)
        }

        buttonIban.setOnClickListener {
            paymentType = "iban"
            buttonEmail.isEnabled = false
            buttonPhone.isEnabled = false
            setVisibilityRecipientSection(R.string.iban_hint)
        }

        buttonPhone.setOnClickListener {
            paymentType = "phone"
            buttonEmail.isEnabled = false
            buttonIban.isEnabled = false
            setVisibilityRecipientSection(R.string.phone_number_hint)
        }

        inputRecipient.doAfterTextChanged {
            when (paymentType) {
                "email" -> setVisibilityAmountSection(isValidEmail(it))
                "iban" -> setVisibilityAmountSection(isValidIban(it))
                "phone" -> setVisibilityAmountSection(isValidPhone(it))
            }
        }

        inputAmount.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                setVisibilityDescriptionSection(true)
            } else {
                setVisibilityDescriptionSection(false)
            }
        }

        inputDescription.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                buttonTransfer.visibility = View.VISIBLE
            } else {
                buttonTransfer.visibility = View.GONE
            }
        }

        buttonTransfer.setOnClickListener {
            viewModel.setRequest(
                inputAmount.text.toString().toBigDecimal(),
                inputDescription.text.toString(),
                inputRecipient.text.toString()
            )
        }
    }

    private fun registerSubscriptions() {
        viewModel.getRequest()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val amount = getString(
                        R.string.cash,
                        it.amountResponded.currency,
                        it.amountResponded.value
                    )
                    val newInstance = TransactionDetailsBottomSheetDialog.newInstance(
                        transaction = R.string.request_details,
                        amount = amount,
                        recipient = it.counterpartyAlias.displayName,
                        type = it.status,
                        date = dateFormatter(it.created),
                        description = it.description
                    )
                    newInstance.show(supportFragmentManager, TAG_REQUESTDETAILSBOTTOMSHEET)

                    CreateNotification(
                        context = this,
                        icon = R.drawable.ic_transaction,
                        title = getString(R.string.request_title),
                        content = getString(
                            R.string.request_detail_success,
                            it.userAliasCreated.displayName,
                            amount,
                            it.counterpartyAlias.displayName
                        )
                    ).invoke()
                },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun setVisibilityRecipientSection(@StringRes inputHint: Int) {
        textRecipientHeader.visibility = View.VISIBLE
        inputRecipient.apply {
            hint = getString(inputHint)
            visibility = View.VISIBLE
            requestFocus()
        }
    }

    private fun setVisibilityAmountSection(isVisible: Boolean) {
        if (isVisible) {
            textAmountHeader.visibility = View.VISIBLE
            textCurrency.visibility = View.VISIBLE
            inputAmount.apply {
                visibility = View.VISIBLE
            }
        } else {
            textAmountHeader.visibility = View.GONE
            textCurrency.visibility = View.GONE
            inputAmount.apply {
                visibility = View.GONE
                inputRecipient.requestFocus()
            }
        }
    }

    private fun setVisibilityDescriptionSection(isVisible: Boolean) {
        if (isVisible) {
            textDescriptionHeader.visibility = View.VISIBLE
            inputDescription.apply {
                visibility = View.VISIBLE
            }
        } else {
            textDescriptionHeader.visibility = View.GONE
            inputDescription.apply {
                visibility = View.GONE
                inputAmount.requestFocus()
            }
        }
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun isValidPhone(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target)
            .matches() && target?.length!! > 10
    }

    private fun isValidIban(target: CharSequence?): Boolean {
        return target?.length!! >= 22
    }

    companion object {
        const val TAG_REQUESTDETAILSBOTTOMSHEET = "RequestDetailsBottomSheetDialog"
    }
}
