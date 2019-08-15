package com.farsheel.mypos.view.product.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.util.Util
import kotlinx.android.synthetic.main.layout_product_list_item.view.*

class ProductAdapter :
    PagedListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        with(holder) {
            bindTo(product)
            product?.let {
                itemView.setOnClickListener {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        private val diffCallback = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean =
                oldItem.itemId == newItem.itemId

            override fun areContentsTheSame(
                oldItem: ProductEntity,
                newItem: ProductEntity
            ): Boolean =
                oldItem == newItem
        }
    }

    class ProductViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_product_list_item,
            parent,
            false
        )
    ) {
        private var product: ProductEntity? = null

        fun bindTo(product: ProductEntity?) {
            this.product = product
            if (product != null) {
                itemView.itemTv.text = product.name
                itemView.itemTotalPriceTv.text = Util.currencyLocale(product.price)

                itemView.let {
                    Glide.with(it)
                        .load(ApiClient.IMAGE_URL + product.image)
                        .into(itemView.itemIv)

                }
            }

        }
    }
}