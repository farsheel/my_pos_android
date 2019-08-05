package com.farsheel.mypos.view.payment.tender


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PaymentTenderFragmentBinding

class PaymentTenderFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentTenderFragment()
    }

    private lateinit var binding: PaymentTenderFragmentBinding


    private lateinit var viewModel: PaymentTenderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentTenderFragmentBinding.bind(
            inflater.inflate(
                R.layout.payment_tender_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PaymentTenderViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.amountToPay.observe(this, Observer {
            viewModel.notifyPropertyChanged(BR.amountToPay)
        })

        viewModel.onSelectCash.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_cashPaymentFragment)
            }
        })

    }

}
