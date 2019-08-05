package com.farsheel.mypos.view.cart

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.*
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.CartEntity
import com.farsheel.mypos.databinding.CartFragmentBinding
import com.farsheel.mypos.util.SwipeToDeleteCallback
import com.farsheel.mypos.util.Util
import com.farsheel.mypos.util.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlinx.android.synthetic.main.layout_cart_item.view.*

class CartFragment : Fragment() {

    companion object {
        private val cartDiffCallBack = object : DiffUtil.ItemCallback<CartEntity>() {
            override fun areItemsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CartEntity,
                newItem: CartEntity
            ): Boolean = false

            override fun getChangePayload(oldItem: CartEntity, newItem: CartEntity): Any? {
                if (oldItem.productPrice.equals(newItem.productPrice)) {
                    return null
                }
                if (oldItem.quantity.equals(newItem.quantity)) {
                    return null
                }
                return super.getChangePayload(oldItem, newItem)
            }
        }

    }

    private lateinit var binding: CartFragmentBinding
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var swipeHandler: SwipeToDeleteCallback
    private lateinit var viewModel: CartViewModel
    private lateinit var cartEditBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = CartFragmentBinding.bind(
            inflater.inflate(
                R.layout.cart_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        binding.viewmodel = viewModel
        cartRcv.layoutManager = LinearLayoutManager(context)
        cartRcv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        itemListAdapter = ItemListAdapter()
        cartRcv.adapter = itemListAdapter
        viewModel.cartList.observe(viewLifecycleOwner, Observer {
            itemListAdapter.submitList(it)
        })

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(cartRcv)

        cartEditBehavior = BottomSheetBehavior.from(cartEditSheet)
        cartEditBehavior.peekHeight = 0
        cartEditBehavior.isHideable = true


        viewModel.itemEditApply.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                context?.let { it1 -> hideKeyboard(it1) }
                viewModel.selectedItem.value?.productPrice =
                    viewModel.enteredPrice.value?.toDouble()!!

                viewModel.selectedItem.value?.quantity =
                    viewModel.enteredQuantity.value?.toDouble()!!

                viewModel.selectedItem.value?.let { item -> viewModel.addToCart(item) }
                cartEditBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })

        viewModel.selectedItem.observe(viewLifecycleOwner, Observer {

            viewModel.enteredPrice.value = it.productPrice.toString()
            viewModel.enteredQuantity.value = it.quantity.toString()
            viewModel.notifyPropertyChanged(BR.enteredQuantity)
            viewModel.notifyPropertyChanged(BR.enteredPrice)
            viewModel.notifyPropertyChanged(BR.selectedItem)
        })

        viewModel.closeBottomSheet.observe(viewLifecycleOwner, Observer {
            cartEditBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            context?.let { it1 -> hideKeyboard(it1) }
        })

        viewModel.cartSubTotal.observe(viewLifecycleOwner, Observer {
            viewModel.notifyPropertyChanged(BR.cartSubTotal)
        })
        viewModel.discountApplied.observe(viewLifecycleOwner, Observer {
            viewModel.notifyPropertyChanged(BR.discountApplied)
        })


        viewModel.onClickPay.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_cartFragment_to_paymentTenderFragment)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val cartEntity = itemListAdapter.currentList?.get(viewHolder.adapterPosition)
                if (cartEntity != null) {
                    viewModel.removeCart(cartEntity)
                    Snackbar.make(
                        viewHolder.itemView,
                        cartEntity.name + " is removed",
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(ContextCompat.getColor(context, R.color.greenUi))
                        .setTextColor(Color.WHITE)
                        .setAction(
                            getString(R.string.undo)
                        ) {
                            viewModel.addToCart(cartEntity)
                        }.show()
                }
            }
        }
    }

    inner class ItemListAdapter :
        PagedListAdapter<CartEntity, ItemListAdapter.CartViewHolder>(cartDiffCallBack) {
        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            val cartEntity = getItem(position)

            with(holder) {
                bindTo(cartEntity)
                cartEntity?.let {
                    itemView.setOnClickListener {
                        viewModel.selectedItem.postValue(cartEntity)
                        if (cartEditBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            cartEditBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            Handler().postDelayed({
                                cartEditBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }, 100)
                        } else {
                            cartEditBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }

                        holder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
            CartViewHolder(parent)


        inner class CartViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_cart_item,
                parent,
                false
            )
        ) {
            private var product: CartEntity? = null

            fun bindTo(product: CartEntity?) {
                this.product = product
                if (product != null) {
                    itemView.itemNameTv.text = product.name
                    itemView.quantityTv.text = product.quantity.toString()
                    itemView.itemTotalPriceTv.text =
                        Util.currencyLocale(product.quantity * product.productPrice)
                    itemView.priceTv.text = Util.currencyLocale(product.productPrice)
                }

            }
        }
    }

}
