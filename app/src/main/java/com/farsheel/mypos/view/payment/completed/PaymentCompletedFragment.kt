package com.farsheel.mypos.view.payment.completed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PaymentCompletedFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.payment_completed_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


class PaymentCompletedFragment : Fragment() {

    private lateinit var binding: PaymentCompletedFragmentBinding
    private val completedViewModel: PaymentCompletedViewModel by viewModel()

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
        binding.viewmodel = completedViewModel

        completedViewModel.startNewSale.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                activity?.onBackPressed()
            }
        })

        completedViewModel.snackbarMessage.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled().let {
                if (it != null) {
                    Snackbar.make(emailLayout, it.message, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(it.color)
                        .setTextColor(Color.WHITE).show()
                }
            }
        })
        completedViewModel.onReceipt.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                completedViewModel.printReceipt()
            }
        })
        completedViewModel.amountPaid.set(args.amountPaid.toDouble())
        completedViewModel.balance.set(args.balance.toDouble())
        completedViewModel.orderId.set(args.orderId)
    }
}
