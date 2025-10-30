package com.dragon.tribe.fire.oc.maker.ui.view

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dragon.tribe.fire.oc.maker.core.base.BaseActivity
import com.dragon.tribe.fire.oc.maker.R
import com.dragon.tribe.fire.oc.maker.core.dialog.ConfirmDialog
import com.dragon.tribe.fire.oc.maker.core.extensions.handleBack
import com.dragon.tribe.fire.oc.maker.core.extensions.handleShare
import com.dragon.tribe.fire.oc.maker.core.extensions.hideNavigation
import com.dragon.tribe.fire.oc.maker.core.extensions.onSingleClick
import com.dragon.tribe.fire.oc.maker.core.extensions.showToast
import com.dragon.tribe.fire.oc.maker.core.extensions.startIntentReverse
import com.dragon.tribe.fire.oc.maker.core.utils.SaveState
import com.dragon.tribe.fire.oc.maker.core.utils.SystemUtils.setLocale
import com.dragon.tribe.fire.oc.maker.core.utils.key.IntentKey
import com.dragon.tribe.fire.oc.maker.databinding.ActivityViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.getValue

class ViewActivity : BaseActivity<ActivityViewBinding>() {
    private val viewModel: ViewViewModel by viewModels()
    private var imagePath: String? = null
    override fun setViewBinding(): ActivityViewBinding {
        return ActivityViewBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        imagePath = intent.getStringExtra(IntentKey.INTENT_KEY)

        imagePath?.let { path ->
            if (path.isNotEmpty()) {
                Glide.with(this).load(path).into(binding.imvImage)
            }
        }
        binding.apply {
            txtShare.isSelected=true
            txtDownLoad.isSelected=true
        }
    }

    override fun viewListener() {
        binding.apply {

            btnDowLoad.onSingleClick {

                lifecycleScope.launch {
                    viewModel.saveImageToExternalStorage(this@ViewActivity, binding.layoutCustomLayer)
                        .collect { result ->
                            when (result) {
                                is SaveState.Loading -> showLoading()
                                is SaveState.Success -> {
                                    dismissLoading(true)
                                }
                                is SaveState.Error -> dismissLoading(true)
                            }
                        }
                }

            }
            btnDelete.onSingleClick {
                confirmDelete()

            }
            btnBack.onSingleClick {
                handleBack()
            }
            btnShare.onSingleClick {
                    val bitmap = createBitmap(binding.layoutCustomLayer.width, binding.layoutCustomLayer.height)
                    val canvas = Canvas(bitmap)
                    binding.layoutCustomLayer.draw(canvas)
                    handleShare(this@ViewActivity,bitmap)

            }
        }
    }


    override fun initText() {

    }
    private fun confirmDelete() {
        val dialog = ConfirmDialog(this, R.string.delete, R.string.do_you_want_to_delete)
        setLocale(this)

        dialog.onYesClick = {
            handleReset()
            dialog.dismiss()
            handleBack()
        }
        dialog.onNoClick = {
            dialog.dismiss()
            hideNavigation()
        }
        dialog.show()
    }


    private fun handleReset() {

        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    showToast(getString(R.string.delete_success))
                } else {
                    showToast(getString(R.string.delete_failed))
                }
            }
        }
    }
}