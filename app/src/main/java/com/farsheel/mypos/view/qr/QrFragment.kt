package com.farsheel.mypos.view.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.QrFragmentBinding
import com.farsheel.mypos.util.Event
import kotlinx.android.synthetic.main.qr_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class QrFragment : Fragment() {

    companion object {
        fun newInstance() = QrFragment()
    }

    private val args: QrFragmentArgs by navArgs()

    private lateinit var binding: QrFragmentBinding
    private val qrViewModel: QrViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QrFragmentBinding.bind(inflater.inflate(R.layout.qr_fragment, container, false))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = qrViewModel


        qrViewModel.textToImageEncode(args.qrCode)


        qrViewModel.qrImage.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Glide.with(this@QrFragment).load(it).into(qrIv)
            }
        })

        qrViewModel.navigateToCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { balance ->
                val action = qrViewModel.amountToPay.value?.let { it1 ->
                    QrFragmentDirections.actionQrFragmentToPaymentCompletedFragment(
                        args.orderId,
                        it1.toFloat(), balance.toFloat()
                    )
                }
                if (action != null) {
                    val navController = view?.findNavController()
                    navController?.popBackStack(R.id.homeFragment, false)
                    navController?.navigate(action)
                }
            }
        })

        refreshLayout.setOnRefreshListener { qrViewModel.subscribeForPaymentStatus(args.orderId) }

        qrViewModel.getOrder(args.orderId).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                refreshLayout.isRefreshing = false
                if (it.paymentStatus.contentEquals("success")) {
                    qrViewModel.navigateToCompleted.value = Event(00.00)
                    qrViewModel.clearDisposables()
                }
            }
        })
    }

}
