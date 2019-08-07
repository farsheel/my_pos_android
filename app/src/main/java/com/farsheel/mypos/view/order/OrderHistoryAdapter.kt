package com.farsheel.mypos.view.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.util.Util
import kotlinx.android.synthetic.main.layout_order_list_item.view.*

class OrderHistoryAdapter :
    PagedListAdapter<OrderDetailEntity, OrderHistoryAdapter.OrderHistoryViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val product = getItem(position)

        with(holder) {
            bindTo(product)
            product?.let {
                itemView.setOnClickListener {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder =
        OrderHistoryViewHolder(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        private val diffCallback = object : DiffUtil.ItemCallback<OrderDetailEntity>() {
            override fun areItemsTheSame(
                oldItem: OrderDetailEntity,
                newItem: OrderDetailEntity
            ): Boolean =
                oldItem.orderId == newItem.orderId

            override fun areContentsTheSame(
                oldItem: OrderDetailEntity,
                newItem: OrderDetailEntity
            ): Boolean =
                oldItem == newItem
        }
    }

    class OrderHistoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_order_list_item,
            parent,
            false
        )
    ) {
        private var orderDetail: OrderDetailEntity? = null

        fun bindTo(orderDetail: OrderDetailEntity?) {
            this.orderDetail = orderDetail
            if (orderDetail != null) {
                itemView.dateTimeTv.text = Util.getDateTimeFromEpochLongOfSeconds(orderDetail.date)

                itemView.orderNoTv.text =
                    itemView.context.getString(R.string.order_no, orderDetail.orderId.toString())
                itemView.itemTotalPriceTv.text = Util.currencyLocale(orderDetail.orderTotal)
            }
        }
    }
}