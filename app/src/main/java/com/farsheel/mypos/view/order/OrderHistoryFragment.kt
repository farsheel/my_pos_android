package com.farsheel.mypos.view.order

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.databinding.OrderHistoryFragmentBinding
import kotlinx.android.synthetic.main.order_history_fragment.*

class OrderHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = OrderHistoryFragment()
    }

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var binding: OrderHistoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OrderHistoryFragmentBinding.bind(
            inflater.inflate(
                R.layout.order_history_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderHistoryViewModel::class.java)
        this.binding.viewmodel = viewModel


        orderHistoryAdapter = OrderHistoryAdapter()
        orderHistoryRcv.adapter = orderHistoryAdapter
        orderHistoryRcv.layoutManager = LinearLayoutManager(context)
        orderHistoryRcv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.productList.observe(this, Observer { pagedNoteList ->
            pagedNoteList?.let { render(pagedNoteList) }
        })
        viewModel.filterTextAll.postValue(null)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_search, menu)

        menu.findItem(R.id.action_search)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_order)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.filterTextAll.postValue("%$newText%")
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun render(pagedList: PagedList<OrderDetailEntity>) {
        orderHistoryAdapter.submitList(pagedList)
    }

}
