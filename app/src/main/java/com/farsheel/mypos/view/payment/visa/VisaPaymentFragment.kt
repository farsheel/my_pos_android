package com.farsheel.mypos.view.payment.visa

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
import com.farsheel.mypos.databinding.PayVisaPaymentFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class VisaPaymentFragment : Fragment() {

    companion object {
        fun newInstance() = VisaPaymentFragment()
    }

    private lateinit var binding: PayVisaPaymentFragmentBinding
    private val visaPaymentViewModel: VisaPaymentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayVisaPaymentFragmentBinding.bind(
            inflater.inflate(
                R.layout.pay_visa_payment_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = visaPaymentViewModel

        visaPaymentViewModel.amountToPay.observe(viewLifecycleOwner, Observer {
            visaPaymentViewModel.amountEntered.set(visaPaymentViewModel.amountToPay.value.toString())
        })

        visaPaymentViewModel.lesserAmountEntered.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.entered_a_lesser_amount_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    visaPaymentViewModel.amountEntered.set(visaPaymentViewModel.amountToPay.value.toString())
                }
                builder.setNeutralButton(getString(R.string.cancel), null)
                val dialog = builder.show()
                context?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setBackgroundColor(ContextCompat.getColor(it, R.color.drawerBackground))
                }
            }
        })

        visaPaymentViewModel.navigateToQr.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { qrCode ->
                val action =
                    VisaPaymentFragmentDirections.actionVisaPaymentFragmentToQrFragment(
                        visaPaymentViewModel.orderId,
                        qrCode
                    )
                view?.findNavController()
                    ?.navigate(action)
            }

        })

        visaPaymentViewModel.errorMessage.observe(viewLifecycleOwner, Observer
        { it ->
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
