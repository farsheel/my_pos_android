package com.farsheel.mypos.view.payment.completed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PaymentCompletedFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.payment_completed_fragment.*


class PaymentCompletedFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentCompletedFragment()
    }

    private lateinit var binding: PaymentCompletedFragmentBinding

    private lateinit var viewModel: PaymentCompletedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentCompletedFragmentBinding.bind(
            LayoutInflater.from(context).inflate(
                R.layout.payment_completed_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    private val args: PaymentCompletedFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PaymentCompletedViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.amountPaid.observe(viewLifecycleOwner, Observer {
            viewModel.notifyPropertyChanged(BR.amountPaid)
        })
        viewModel.startNewSale.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                activity?.onBackPressed()
            }
        })

        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled().let {
                if (it != null) {
                    Snackbar.make(emailLayout, it.message, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(it.color)
                        .setTextColor(Color.WHITE).show()
                }
            }
        })

        viewModel.onReceipt.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                viewModel.printReceipt()
            }
        })

        viewModel.amountPaid.value = args.amountPaid.toDouble()
        viewModel.balance.value = args.balance.toDouble()
        viewModel.orderId.value = args.orderId

    }
}
