package com.farsheel.mypos.view.product.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.farsheel.mypos.R
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.databinding.AddEditProductFragmentBinding
import com.farsheel.mypos.util.listeners.CategoryClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_edit_product_fragment.*
import kotlinx.android.synthetic.main.add_edit_product_fragment.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File


class AddEditProductFragment : Fragment() {

    private lateinit var binding: AddEditProductFragmentBinding
    private val addEditProductViewModel: AddEditProductViewModel by viewModel()
    private lateinit var categoryAdapter: CategorySelectorAdapter
    private lateinit var categoryBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddEditProductFragmentBinding.bind(
            inflater.inflate(
                R.layout.add_edit_product_fragment,
                container,
                false
            )
        )
        categoryBehavior = BottomSheetBehavior.from(binding.root.bottomSheet)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = addEditProductViewModel

        categoryAdapter = CategorySelectorAdapter(object : CategoryClickListener {
            override fun onClickCategory(category: CategoryEntity) {
                addEditProductViewModel.category.set(category.catId.toString())
                addEditProductViewModel.categoryString.set(category.name)
                categoryBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        catRcv.adapter = categoryAdapter

        catRcv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        addEditProductViewModel.categoryList().observe(this, Observer { pagedNoteList ->
            pagedNoteList?.let { render(pagedNoteList) }
        })
        addEditProductViewModel.searchCategory(null)

        categoryBehavior.isHideable = true
        categoryBehavior.peekHeight = 0
        categoryBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        addEditProductViewModel.showCatSpinner.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                categoryBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        addEditProductViewModel.selectImage.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                openImagePicker()
            }
        })

        addEditProductViewModel.image.observe(this, Observer {
            Glide.with(this).load(it).into(productImageIv)
        })
        addEditProductViewModel.categoryFilter.observe(this, Observer {
            addEditProductViewModel.searchCategory(it)
        })

        addEditProductViewModel.snackbarMessage.observe(this, Observer { it ->
            it.getContentIfNotHandled().let {
                if (it != null) {
                    Snackbar.make(saveProductFab, it.message, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(it.color)
                        .setTextColor(Color.WHITE).show()
                }
            }
        })

        addEditProductViewModel.saveError.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let { message ->
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(message)
                    builder.show()
                }
            }
        })
    }

    private fun openImagePicker() {

        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
            .folderMode(true) // folder mode (false by default)
            .toolbarFolderTitle("Folder") // folder selection title
            .toolbarImageTitle("Tap to select") // image selection title
            .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
            .includeVideo(false) // Show video on image picker
            .single() // single mode
            .theme(R.style.ImagePickerTheme)
            .showCamera(true) // show camera or not (true by default)
            .enableLog(false) // disabling log
            .start() // start image picker activity with request
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            addEditProductViewModel.image.postValue(File(image.path))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun render(pagedCatList: PagedList<CategoryEntity>) {
        categoryAdapter.submitList(pagedCatList)
    }
}
