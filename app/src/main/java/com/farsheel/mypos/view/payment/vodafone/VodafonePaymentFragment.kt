package com.farsheel.mypos.view.payment.vodafone

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PayVodafonePaymentFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class VodafonePaymentFragment : Fragment() {



    private lateinit var binding: PayVodafonePaymentFragmentBinding
    private val vodafonePaymentViewModel: VodafonePaymentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayVodafonePaymentFragmentBinding.bind(
            inflater.inflate(
                R.layout.pay_vodafone_payment_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = vodafonePaymentViewModel

        vodafonePaymentViewModel.amountToPay.observe(viewLifecycleOwner, Observer {
            vodafonePaymentViewModel.amountEntered.set(vodafonePaymentViewModel.amountToPay.value.toString())

        })

        vodafonePaymentViewModel.lesserAmountEntered.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.entered_a_lesser_amount_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    vodafonePaymentViewModel.amountEntered.set(vodafonePaymentViewModel.amountToPay.value.toString())
                }
                builder.setNeutralButton(getString(R.string.cancel), null)
                val dialog = builder.show()
                context?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setBackgroundColor(ContextCompat.getColor(it, R.color.drawerBackground))
                }
            }
        })

        vodafonePaymentViewModel.navigateToCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { balance ->
                val action = vodafonePaymentViewModel.amountToPay.value?.let { it1 ->
                    VodafonePaymentFragmentDirections.actionVodafonePaymentFragmentToPaymentCompletedFragment(
                        vodafonePaymentViewModel.orderId,
                        it1.toFloat(), balance.toFloat()
                    )
                }
                if (action != null) {
                    view?.findNavController()
                        ?.navigate(action)
                }
            }
        })

        vodafonePaymentViewModel.errorMessage.observe(viewLifecycleOwner, Observer { it ->
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