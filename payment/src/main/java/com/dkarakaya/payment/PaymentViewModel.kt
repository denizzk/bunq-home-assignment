package com.dkarakaya.payment

import androidx.lifecycle.ViewModel
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.`object`.Pointer
import com.bunq.sdk.model.generated.endpoint.Payment
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

class PaymentViewModel @Inject constructor(
    createApiContext: CreateApiContext,
    apiContextFilePath: ApiContextFilePath
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val apiContextSubject = BehaviorSubject.create<ApiContext>()
    private val contextSavedSubject = BehaviorSubject.create<Unit>()
    private val contextLoadedSubject = BehaviorSubject.create<Unit>()

    // Input
    private val amountInput = BehaviorSubject.create<BigDecimal>()
    private val descriptionInput = BehaviorSubject.create<String>()
    private val recipientInput = BehaviorSubject.create<String>()

    // Output
    private val paymentIdSubject = BehaviorSubject.create<Int>()
    private val paymentOutput = BehaviorSubject.create<Payment>()

    init {
        // creates or restores api context on the given path
        createApiContext(apiContextFilePath()!!)
            .subscribeBy(
                onSuccess = apiContextSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // save api context to file on each new payment
        paymentIdSubject
            .withLatestFrom(apiContextSubject) { _, apiContext ->
                Observable.fromCallable { apiContext.save(ApiContextFilePath.filePath!!) }
            }
            .switchMap { it }
            .subscribeBy(
                onNext = contextSavedSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // load api context
        apiContextSubject
            .switchMap { Observable.fromCallable { BunqContext.loadApiContext(it) } }
            .subscribeBy(
                onNext = contextLoadedSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // make a new payment
        amountInput
            .withLatestFrom(
                descriptionInput,
                recipientInput
            ) { amount, description, recipient ->
                Observable.fromCallable { makePayment(amount, description, recipient) }
            }
            .switchMap { it }
            .subscribeBy(
                onNext = paymentIdSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // get the payment details
        Observables
            .combineLatest(
                contextLoadedSubject,
                paymentIdSubject
            ) { _, id ->
                Observable.fromCallable { getPayment(id) }
            }
            .switchMap { it }
            .subscribeBy(
                onNext = paymentOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Inputs
     */

    fun setPayment(amount: BigDecimal, description: String, recipient: String) {
        // set default to avoid errors
        recipientInput.onNext(RECIPIENT)
        descriptionInput.onNext(description)
        amountInput.onNext(amount)
    }

    /**
     * Outputs
     */

    fun getPayment(): Observable<Payment> = paymentOutput

    /**
     * Methods
     */

    private fun makePayment(amount: BigDecimal, description: String, recipient: String): Int {
        return Payment.create(
            Amount(
                amount.toString(),
                CURRENCY_EURO
            ),
            Pointer(POINTER_TYPE_EMAIL, recipient),
            description
        )
            .value
    }

    private fun getPayment(id: Int): Payment {
        return Payment.get(id).value
    }

    companion object {
        const val CURRENCY_EURO = "EUR"
        const val POINTER_TYPE_EMAIL = "EMAIL"
        const val RECIPIENT = "sugardaddy@bunq.com"
    }
}
