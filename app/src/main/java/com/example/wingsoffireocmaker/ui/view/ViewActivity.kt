package com.example.wingsoffireocmaker.ui.view

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
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.core.dialog.ConfirmDialog
import com.example.wingsoffireocmaker.core.extensions.handleBack
import com.example.wingsoffireocmaker.core.extensions.handleShare
import com.example.wingsoffireocmaker.core.extensions.hideNavigation
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.showToast
import com.example.wingsoffireocmaker.core.extensions.startIntentReverse
import com.example.wingsoffireocmaker.core.utils.SaveState
import com.example.wingsoffireocmaker.core.utils.SystemUtils.setLocale
import com.example.wingsoffireocmaker.core.utils.key.IntentKey
import com.example.wingsoffireocmaker.databinding.ActivityViewBinding
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