package com.farsheel.mypos.view.category.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.util.listeners.CategoryClickListener
import kotlinx.android.synthetic.main.layout_category_list_item.view.*

class CategoryAdapter(private val categoryClickListener: CategoryClickListener?) :
    PagedListAdapter<CategoryEntity, CategoryAdapter.CategoryViewHolder>(diffCallback) {


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val note = getItem(position)

        with(holder) {
            bindTo(note)
            note?.let {
                itemView.setOnClickListener {
                    categoryClickListener?.onClickCategory(note)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        private val diffCallback = object : DiffUtil.ItemCallback<CategoryEntity>() {
            override fun areItemsTheSame(
                oldItem: CategoryEntity,
                newItem: CategoryEntity
            ): Boolean =
                oldItem.catId == newItem.catId

            override fun areContentsTheSame(
                oldItem: CategoryEntity,
                newItem: CategoryEntity
            ): Boolean =
                oldItem == newItem
        }

    }

    class CategoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_category_list_item,
            parent,
            false
        )
    ) {
        private var categoryEntity: CategoryEntity? = null

        @SuppressLint("SetTextI18n")
        fun bindTo(categoryEntity: CategoryEntity?) {
            this.categoryEntity = categoryEntity
            if (categoryEntity != null) {
                itemView.catNameTv.text = categoryEntity.name
                itemView.catDescriptionTv.text = categoryEntity.description
            }

        }
    }
}
