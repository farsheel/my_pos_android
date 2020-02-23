package com.farsheel.mypos.view.product.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.databinding.ProductListFragmentBinding
import kotlinx.android.synthetic.main.product_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProductListFragment : Fragment() {

    private lateinit var binding: ProductListFragmentBinding
    private val productListViewModel: ProductListViewModel by viewModel()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = ProductListFragmentBinding.bind(
            inflater.inflate(
                R.layout.product_list_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = productListViewModel

        productListViewModel.navigateToAddNew.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_productListFragment_to_addEditProductFragment)
            }
        })

        productAdapter = ProductAdapter()
        productListRcv.adapter = productAdapter
        productListRcv.layoutManager = LinearLayoutManager(context)
        productListRcv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        productListViewModel.productList.observe(this, Observer { pagedNoteList ->
            pagedNoteList?.let { render(pagedNoteList) }
        })
        productListViewModel.filterTextAll.postValue(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_search, menu)

        menu.findItem(R.id.action_search)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_products)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                productListViewModel.filterTextAll.postValue("%$newText%")
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun render(pagedNoteList: PagedList<ProductEntity>) {
        productAdapter.submitList(pagedNoteList)
    }
}
