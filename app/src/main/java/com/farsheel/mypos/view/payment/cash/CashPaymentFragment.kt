package com.farsheel.mypos.view.payment.cash

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.CashPaymentFragmentBinding

class CashPaymentFragment : Fragment() {

    companion object {
        fun newInstance() = CashPaymentFragment()
    }

    private lateinit var binding: CashPaymentFragmentBinding
    private lateinit var viewModel: CashPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CashPaymentFragmentBinding.bind(
            inflater.inflate(
                R.layout.cash_payment_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CashPaymentViewModel::class.java)
        binding.viewmodel = viewModel


        viewModel.amountToPay.observe(viewLifecycleOwner, Observer {
            viewModel.amountEntered.postValue(viewModel.amountToPay.value.toString())
            viewModel.notifyPropertyChanged(BR.amountToPay)
            viewModel.notifyPropertyChanged(BR.amountEntered)
        })

        viewModel.amountEntered.observe(viewLifecycleOwner, Observer {
            viewModel.notifyPropertyChanged(BR.amountEntered)
        })

        viewModel.lesserAmountEntered.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.entered_a_lesser_amount_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.amountEntered.value = viewModel.amountToPay.value.toString()
                }
                builder.setNeutralButton(getString(R.string.cancel), null)
                val dialog = builder.show()
                context?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setBackgroundColor(ContextCompat.getColor(it, R.color.darkColor))
                }
            }
        })

        viewModel.navigateToCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { balance ->
                val action = viewModel.amountToPay.value?.let { it1 ->
                    CashPaymentFragmentDirections.actionCashPaymentFragmentToPaymentCompletedFragment(
                        viewModel.orderId,
                        it1.toFloat(), balance.toFloat()
                    )
                }
                if (action != null) {
                    view?.findNavController()
                        ?.navigate(action)
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { message ->
                context?.let {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(it)
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })
    }


}
