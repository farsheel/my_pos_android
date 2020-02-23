package com.farsheel.mypos.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.LoginFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.bind(
            LayoutInflater.from(context).inflate(
                R.layout.login_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = loginViewModel

        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { message ->
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })
        loginViewModel.loginSuccess.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                view?.findNavController()
                    ?.navigate(R.id.action_loginFragment_to_homeFragment)
            }
        })
    }
}
