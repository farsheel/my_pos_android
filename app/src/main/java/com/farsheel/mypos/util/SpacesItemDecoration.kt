package com.farsheel.mypos.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SpacesItemDecoration(private val columnCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % columnCount // item column

            outRect.left = spacing - column * spacing / columnCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / columnCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < columnCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom

    }

}