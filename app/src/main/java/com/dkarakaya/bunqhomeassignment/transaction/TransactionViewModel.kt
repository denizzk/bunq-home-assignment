package com.dkarakaya.bunqhomeassignment.transaction

import androidx.lifecycle.ViewModel
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.http.Pagination
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank
import com.bunq.sdk.model.generated.endpoint.Payment
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.CreateApiContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class TransactionViewModel @Inject constructor(
    createApiContext: CreateApiContext,
    apiContextFilePath: ApiContextFilePath
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val apiContextSubject = BehaviorSubject.create<ApiContext>()
    private val contextSavedSubject = BehaviorSubject.create<Unit>()
    private val contextLoadedSubject = BehaviorSubject.create<Unit>()

    // Input
    private val updateListInput = BehaviorSubject.create<Unit>()

    // Output
    private val monetaryAccountOutput = BehaviorSubject.create<MonetaryAccountBank>()
    private val allPaymentsOutput = BehaviorSubject.create<MutableList<Payment>>()

    init {
        // creates or restores api context on the given path
        createApiContext(apiContextFilePath()!!)
            .subscribeBy(
                onSuccess = apiContextSubject::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // save api context to file on each new payment
        apiContextSubject
            .switchMap { apiContext ->
                Observable.fromCallable { apiContext.save(ApiContextFilePath.filePath!!) }
            }
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

        // get monetary account of the user
        apiContextSubject
            .switchMap { Observable.fromCallable { BunqContext.getUserContext().primaryMonetaryAccountBank } }
            .subscribeBy(
                onNext = monetaryAccountOutput::onNext,
                onError = Timber::e
            )
            .addTo(disposables)

        // get all payments from the user's monetary account
        Observables
            .combineLatest(updateListInput, contextLoadedSubject)
            .switchMap { Observable.fromCallable { getAllPayments() } }
            .subscribeBy(
                onNext = allPaymentsOutput::onNext,
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

    fun updateList() {
        updateListInput.onNext(Unit)
    }

    /**
     * Outputs
     */

    fun getMonetaryAccount(): Observable<MonetaryAccountBank> = monetaryAccountOutput

    fun getPayments(): Observable<MutableList<Payment>> = allPaymentsOutput

    /**
     * Methods
     */

    private fun getAllPayments(): MutableList<Payment> {
        val pagination = Pagination()
        pagination.count = 200
        return Payment
            .list(null, pagination.urlParamsCountOnly)
            .value
    }
}
