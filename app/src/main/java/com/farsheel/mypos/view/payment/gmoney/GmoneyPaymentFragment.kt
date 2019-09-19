package com.farsheel.mypos.view.payment.gmoney

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PayGmoneyPaymentFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class GmoneyPaymentFragment : Fragment() {


    private lateinit var binding: PayGmoneyPaymentFragmentBinding
    private val cashPaymentViewModel: GmoneyPaymentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayGmoneyPaymentFragmentBinding.bind(
            inflater.inflate(
                R.layout.pay_gmoney_payment_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewmodel = cashPaymentViewModel


        cashPaymentViewModel.amountToPay.observe(viewLifecycleOwner, Observer {
            cashPaymentViewModel.amountEntered.set(cashPaymentViewModel.amountToPay.value.toString())
        })


        cashPaymentViewModel.lesserAmountEntered.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.entered_a_lesser_amount_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    cashPaymentViewModel.amountEntered.set(cashPaymentViewModel.amountToPay.value.toString())
                }
                builder.setNeutralButton(getString(R.string.cancel), null)
                val dialog = builder.show()
                context?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setBackgroundColor(ContextCompat.getColor(it, R.color.drawerBackground))
                }
            }
        })

        cashPaymentViewModel.navigateToCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { balance ->
                val action = cashPaymentViewModel.amountToPay.value?.let { it1 ->
                    GmoneyPaymentFragmentDirections.actionGmoneyPaymentFragmentToPaymentCompletedFragment(
                        cashPaymentViewModel.orderId,
                        it1.toFloat(), balance.toFloat()
                    )
                }
                if (action != null) {
                    view?.findNavController()
                        ?.navigate(action)
                }
            }
        })

        cashPaymentViewModel.errorMessage.observe(viewLifecycleOwner, Observer { it ->
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
