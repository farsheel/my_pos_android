package com.farsheel.mypos.view.product.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.databinding.AddEditProductFragmentBinding
import com.farsheel.mypos.util.listeners.CategoryClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_edit_product_fragment.*
import kotlinx.android.synthetic.main.add_edit_product_fragment.view.*


class AddEditProductFragment : Fragment() {

    private lateinit var binding: AddEditProductFragmentBinding
    private lateinit var viewModel: AddEditProductViewModel
    private lateinit var categoryAdapter: CategorySelectorAdapter
    private lateinit var categoryBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddEditProductFragmentBinding.bind(
            inflater.inflate(
                R.layout.add_edit_product_fragment,
                container,
                false
            )
        )
        categoryBehavior = BottomSheetBehavior.from(binding.root.bottomSheet)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddEditProductViewModel::class.java)
        binding.viewmodel = viewModel

        categoryAdapter = CategorySelectorAdapter(object : CategoryClickListener {
            override fun onClickCategory(category: CategoryEntity) {
                viewModel.category.postValue(category.catId.toString())
                viewModel.categoryString.postValue(category.name)
                viewModel.validate(true)
                categoryBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        catRcv.adapter = categoryAdapter

        catRcv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        viewModel.categoryList.observe(this, Observer { pagedNoteList ->
            pagedNoteList?.let { render(pagedNoteList) }
        })
        viewModel.categoryFilter.postValue(null)

        categoryBehavior.isHideable = true
        categoryBehavior.peekHeight = 0
        categoryBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.showCatSpinner.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                categoryBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        viewModel.snackbarMessage.observe(this, Observer { it ->
            it.getContentIfNotHandled().let {
                if (it != null) {
                    Snackbar.make(saveProductFab, it.message, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(it.color)
                        .setTextColor(Color.WHITE).show()
                }
            }
        })

        viewModel.saveError.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { message ->
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })

        observeChanges()
    }

    private fun observeChanges() {
        viewModel.itemUpc.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.mUpcError.postValue(null)
            }
        })
        viewModel.itemName.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.mNameError.postValue(null)
            }
        })
        viewModel.price.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.mPriceError.postValue(null)
            }
        })
        viewModel.category.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.mCategoryError.postValue(null)
            }
        })

    }

    private fun render(pagedCatList: PagedList<CategoryEntity>) {
        categoryAdapter.submitList(pagedCatList)
    }
}
