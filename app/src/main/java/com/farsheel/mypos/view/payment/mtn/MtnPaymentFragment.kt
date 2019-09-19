package com.farsheel.mypos.view.payment.mtn

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
import com.farsheel.mypos.databinding.PayMtnPaymentFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class MtnPaymentFragment : Fragment() {

    companion object {
        fun newInstance() = MtnPaymentFragment()
    }

    private lateinit var binding: PayMtnPaymentFragmentBinding
    private val mtnPaymentViewModel: MtnPaymentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayMtnPaymentFragmentBinding.bind(
            inflater.inflate(
                R.layout.pay_mtn_payment_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = mtnPaymentViewModel

        mtnPaymentViewModel.amountToPay.observe(viewLifecycleOwner, Observer {
            mtnPaymentViewModel.amountEntered.set(mtnPaymentViewModel.amountToPay.value.toString())

        })

        mtnPaymentViewModel.lesserAmountEntered.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.entered_a_lesser_amount_message))
                builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    mtnPaymentViewModel.amountEntered.set( mtnPaymentViewModel.amountToPay.value.toString())
                }
                builder.setNeutralButton(getString(R.string.cancel), null)
                val dialog = builder.show()
                context?.let {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setBackgroundColor(ContextCompat.getColor(it, R.color.drawerBackground))
                }
            }
        })

        mtnPaymentViewModel.navigateToCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { balance ->
                val action = mtnPaymentViewModel.amountToPay.value?.let { it1 ->
                    MtnPaymentFragmentDirections.actionMtnPaymentFragmentToPaymentCompletedFragment(
                        mtnPaymentViewModel.orderId,
                        it1.toFloat(), balance.toFloat()
                    )
                }
                if (action != null) {
                    view?.findNavController()
                        ?.navigate(action)
                }
            }
        })

        mtnPaymentViewModel.errorMessage.observe(viewLifecycleOwner, Observer { it ->
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
