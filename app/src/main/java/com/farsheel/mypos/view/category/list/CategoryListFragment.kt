package com.farsheel.mypos.view.category.list

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
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.databinding.CategoryListFragmentBinding
import kotlinx.android.synthetic.main.product_list_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryListFragment : Fragment() {

    private lateinit var binding: CategoryListFragmentBinding
    private  val categoryListViewModel: CategoryListViewModel by viewModel()

    private lateinit var categoryAdapter: CategoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = CategoryListFragmentBinding.bind(
            inflater.inflate(
                R.layout.category_list_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = categoryListViewModel

        categoryListViewModel.navigateToAddNew.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_categoryListFragment_to_addEditCategoryFragment)
            }
        })

        categoryAdapter = CategoryAdapter(null)
        productListRcv.adapter = categoryAdapter
        productListRcv.layoutManager = LinearLayoutManager(context)
        productListRcv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        categoryListViewModel.getCategoryList().observe(this, Observer { pagedNoteList ->
            pagedNoteList?.let { render(pagedNoteList) }
        })
        categoryListViewModel.updateSearch(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_search, menu)

        menu.findItem(R.id.action_search)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_categories)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                categoryListViewModel.updateSearch("%$newText%")
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun render(pagedNoteList: PagedList<CategoryEntity>) {
        categoryAdapter.submitList(pagedNoteList)
    }
}
