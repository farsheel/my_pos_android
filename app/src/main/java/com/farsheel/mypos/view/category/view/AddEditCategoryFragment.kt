package com.farsheel.mypos.view.category.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.farsheel.mypos.R
import com.farsheel.mypos.databinding.AddEditCategoryFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_edit_category_fragment.*


class AddEditCategoryFragment : Fragment() {


    private lateinit var binding: AddEditCategoryFragmentBinding
    private lateinit var viewModel: AddEditCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AddEditCategoryFragmentBinding.bind(
            inflater.inflate(
                R.layout.add_edit_category_fragment,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddEditCategoryViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.snackbarMessage.observe(this, Observer { it ->
            it.getContentIfNotHandled().let {
                if (it != null) {
                    Snackbar.make(saveCatFab, it.message, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(it.color)
                        .setTextColor(Color.WHITE).show()
                }
            }
        })

        viewModel.saveError.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { message ->
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })
    }
}
