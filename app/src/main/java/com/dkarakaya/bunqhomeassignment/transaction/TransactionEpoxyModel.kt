package com.dkarakaya.bunqhomeassignment.transaction

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bunq.sdk.model.generated.endpoint.Payment
import com.dkarakaya.bunqhomeassignment.R
import com.dkarakaya.core.util.shortDateFormatter

@EpoxyModelClass
abstract class TransactionEpoxyModel : EpoxyModelWithHolder<TransactionEpoxyModel.PaymentHolder>() {

    @EpoxyAttribute
    lateinit var transaction: Payment

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun getDefaultLayout(): Int {
        return R.layout.item_transaction
    }

    override fun bind(holder: PaymentHolder) {
        holder.apply {
            if (transaction.subType == "PAYMENT") {
                Glide
                    .with(icon)
                    .load(R.drawable.ic_left_arrow)
                    .into(icon)
            } else {
                Glide
                    .with(icon)
                    .load(R.drawable.ic_right_arrow)
                    .into(icon)
            }
            textHeadline.text = transaction.counterpartyAlias.displayName
            textDate.text =
                shortDateFormatter(transaction.created)
            textContent.text = transaction.subType
            textCash.text = root.context.getString(
                R.string.cash,
                transaction.amount.currency,
                transaction.amount.value
            )
            root.setOnClickListener(onClickListener)
        }
    }

    inner class PaymentHolder : EpoxyHolder() {

        lateinit var root: ConstraintLayout
        lateinit var icon: ImageView
        lateinit var textHeadline: TextView
        lateinit var textDate: TextView
        lateinit var textContent: TextView
        lateinit var textCash: TextView

        override fun bindView(itemView: View) {
            root = itemView.findViewById(R.id.layoutPayment)
            icon = itemView.findViewById(R.id.iconPayment)
            textHeadline = itemView.findViewById(R.id.textHeadline)
            textDate = itemView.findViewById(R.id.textItemDate)
            textContent = itemView.findViewById(R.id.textContent)
            textCash = itemView.findViewById(R.id.textCash)
        }
    }
}
