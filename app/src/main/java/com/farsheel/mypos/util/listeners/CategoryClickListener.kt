package com.farsheel.mypos.util.listeners

import com.farsheel.mypos.data.model.CategoryEntity

interface CategoryClickListener {
    fun onClickCategory(category: CategoryEntity)
}