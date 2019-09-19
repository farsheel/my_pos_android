package com.farsheel.mypos.view.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.SplashFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class SplashFragment : Fragment() {

    private val splashViewModel: SplashViewModel by viewModel()
    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SplashFragmentBinding.bind(
            LayoutInflater.from(context).inflate(
                R.layout.splash_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = splashViewModel

        splashViewModel.openNext.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { action ->
                view?.findNavController()
                    ?.navigate(action)
            }
        })
    }
}
