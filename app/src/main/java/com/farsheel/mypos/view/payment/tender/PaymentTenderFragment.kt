package com.farsheel.mypos.view.payment.tender


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.PaymentTenderFragmentBinding
import com.farsheel.mypos.util.Util
import kotlinx.android.synthetic.main.payment_tender_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentTenderFragment : Fragment() {

    private lateinit var binding: PaymentTenderFragmentBinding

    private val paymentTenderViewModel: PaymentTenderViewModel by viewModel()

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
        binding.viewmodel = paymentTenderViewModel

        paymentTenderViewModel.amountToPay.observe(this, Observer {
            amountTv.text = Util.currencyLocale(it)
        })

        paymentTenderViewModel.onSelectCash.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_paymentTenderFragment_to_cashPaymentFragment)
            }
        })


        paymentTenderViewModel.onSelectAirtel.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_airtelPaymentFragment)
            }
        })


        paymentTenderViewModel.onSelectMtn.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_mtnPaymentFragment)
            }
        })


        paymentTenderViewModel.onSelectVodafone.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_vodafonePaymentFragment)
            }
        })


        paymentTenderViewModel.onSelectGmoney.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_gmoneyPaymentFragment)
            }
        })


        paymentTenderViewModel.onSelectMasterpass.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_masterpassPaymentFragment)
            }
        })

        paymentTenderViewModel.onSelectVisa.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()?.navigate(R.id.action_paymentTenderFragment_to_visaPaymentFragment)
            }
        })
    }

}
