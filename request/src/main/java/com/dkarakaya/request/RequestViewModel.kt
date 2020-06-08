package com.dkarakaya.request

import androidx.lifecycle.ViewModel
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.`object`.Pointer
import com.bunq.sdk.model.generated.endpoint.RequestInquiry
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

class RequestViewModel @Inject constructor(
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
    private val requestIdSubject = BehaviorSubject.create<Int>()
    private val requestOutput = BehaviorSubject.create<RequestInquiry>()

    init {
        // creates or restores api context on the given path
        createApiContext(apiContextFilePath()!!)
            .subscribeBy(
                onSuccess = apiContextSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // save api context to file on each new payment
        requestIdSubject
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

        // make a new request
        amountInput
            .withLatestFrom(
                descriptionInput,
                recipientInput
            ) { amount, description, recipient ->
                Observable.fromCallable { makeRequest(amount, description, recipient) }
            }
            .switchMap { it }
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onNext = requestIdSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // get the request details
        Observables
            .combineLatest(
                contextLoadedSubject,
                requestIdSubject
            ) { _, id ->
                Observable.fromCallable { getRequest(id) }
            }
            .switchMap { it }
            .subscribeOn(Schedulers.computation())
            .subscribeBy(
                onNext = requestOutput::onNext,
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

    fun setRequest(amount: BigDecimal, description: String, recipient: String) {
        // set default to avoid errors
        recipientInput.onNext(RECIPIENT)
        descriptionInput.onNext(description)
        amountInput.onNext(amount)
    }

    /**
     * Outputs
     */

    fun getRequest(): Observable<RequestInquiry> = requestOutput

    /**
     * Methods
     */

    private fun makeRequest(amount: BigDecimal, description: String, recipient: String): Int {
        return RequestInquiry.create(
            Amount(amount.toString(), CURRENCY_EURO),
            Pointer(POINTER_TYPE_EMAIL, recipient),
            description,
            false
        )
            .value
    }

    private fun getRequest(id: Int): RequestInquiry {
        return RequestInquiry.get(id).value
    }

    companion object {
        const val CURRENCY_EURO = "EUR"
        const val POINTER_TYPE_EMAIL = "EMAIL"
        const val RECIPIENT = "sugardaddy@bunq.com"
    }

}
