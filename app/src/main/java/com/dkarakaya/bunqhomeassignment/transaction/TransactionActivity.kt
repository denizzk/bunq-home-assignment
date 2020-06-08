package com.dkarakaya.bunqhomeassignment.transaction

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bunq.sdk.model.generated.endpoint.Payment
import com.dkarakaya.bunqhomeassignment.R
import com.dkarakaya.core.TransactionDetailsBottomSheetDialog
import com.dkarakaya.core.util.ApiContextFilePath
import com.dkarakaya.core.util.dateFormatter
import com.dkarakaya.core.viewmodel.ViewModelFactory
import com.dkarakaya.request.RequestActivity
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class TransactionActivity : DaggerAppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TransactionViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(TransactionViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Shouldn't be used but used to makePayment call stable
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setApiContextFile()

        registerListeners()
        registerSubscriptions()

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateList()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun setApiContextFile() {
        val path: File? = this.getExternalFilesDir(null)
        ApiContextFilePath.filePath = File(path, "api-context2.txt").absolutePath
    }

    private fun registerListeners() {
        buttonRequest.setOnClickListener {
            val intent = Intent(this, RequestActivity::class.java)
            startActivity(intent)
        }
        buttonPayment.setOnClickListener {
            val intent = Intent(this, com.dkarakaya.payment.PaymentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerSubscriptions() {
        viewModel.getMonetaryAccount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    textUser.text = it.displayName
                    textBalanceValue.text = getString(
                        R.string.cash,
                        it.balance.currency,
                        it.balance.value
                    )
                },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    private fun initRecyclerView() {
        val controller =
            TransactionController()
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = controller.adapter
            layoutManager = LinearLayoutManager(context)
        }
        showPayments(controller)
        controller.transactionClickListener = { transaction ->
            showTransactionDetails(transaction)
        }
    }

    private fun showTransactionDetails(transaction: Payment) {
        val newInstance = TransactionDetailsBottomSheetDialog.newInstance(
            transaction = R.string.transaction_details,
            amount = getString(
                R.string.cash,
                transaction.amount.currency,
                transaction.amount.value
            ),
            recipient = transaction.counterpartyAlias.displayName,
            type = transaction.type,
            date = dateFormatter(transaction.created),
            description = transaction.description
        )
        newInstance.show(
            supportFragmentManager,
            TAG_PAYMENTDETAILSBOTTOMSHEET
        )
    }

    private fun showPayments(controller: TransactionController) {
        viewModel.getPayments()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { controller.transactions = it },
                onError = Timber::e
            )
            .addTo(disposables)
    }

    companion object {
        const val TAG_PAYMENTDETAILSBOTTOMSHEET = "TransactionDetailsBottomSheetDialog"
    }
}
