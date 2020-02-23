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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import org.koin.android.viewmodel.ext.android.viewModel

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
    private val cartViewModel: CartViewModel by viewModel()
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

        binding.viewmodel = cartViewModel
        cartRcv.layoutManager = LinearLayoutManager(context)
        cartRcv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        itemListAdapter = ItemListAdapter()
        cartRcv.adapter = itemListAdapter
        cartViewModel.getCartList().observe(viewLifecycleOwner, Observer {
            itemListAdapter.submitList(it)
            Handler().postDelayed({
                if (itemListAdapter.itemCount == 0){
                    totalsLayout.visibility = View.GONE
                }else{
                    totalsLayout.visibility = View.VISIBLE
                }
            },100)
        })

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(cartRcv)

        cartEditBehavior = BottomSheetBehavior.from(cartEditSheet)
        cartEditBehavior.peekHeight = 0
        cartEditBehavior.isHideable = true


        cartViewModel.itemEditApply.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                context?.let { it1 -> hideKeyboard(it1) }
                cartViewModel.selectedItem.value?.productPrice =
                    cartViewModel.enteredPrice.value?.toDouble()!!

                cartViewModel.selectedItem.value?.quantity =
                    cartViewModel.enteredQuantity.value?.toDouble()!!

                cartViewModel.selectedItem.value?.let { item -> cartViewModel.addToCart(item) }
                cartEditBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })

        cartViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            cartViewModel.enteredPrice.value = it.productPrice.toString()
            cartViewModel.enteredQuantity.value = it.quantity.toString()
            editNameTv.text = it.name
            quantityEt.setText(it.quantity.toString())
            priceEt.setText(it.productPrice.toString())

        })

        cartViewModel.closeBottomSheet.observe(viewLifecycleOwner, Observer {
            cartEditBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            context?.let { it1 -> hideKeyboard(it1) }
        })

        cartViewModel.getSubTotal().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                subTotalTv.text = getString(R.string.subtotal, Util.currencyLocale(it))
            }
        })
        cartViewModel.cartVATTotal.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                payBtn.text = getString(
                    R.string.pay,
                    Util.currencyLocale(it)
                )

                totalTv.text = getString(
                    R.string.total,
                    Util.currencyLocale(it)
                )
            }
        })

        cartViewModel.cartVAT.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                vatTv.text = getString(R.string.vat, Util.currencyLocale(it))
            }
        })
        cartViewModel.onClickPay.observe(viewLifecycleOwner, Observer {
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
                    cartViewModel.removeCart(cartEntity)
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
                            cartViewModel.addToCart(cartEntity)
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
                        cartViewModel.selectedItem.postValue(cartEntity)
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

