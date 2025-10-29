package com.example.wingsoffireocmaker.ui.success

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.extensions.handleBack
import com.example.wingsoffireocmaker.core.extensions.handleShare
import com.example.wingsoffireocmaker.core.extensions.loadImageGlide
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.showToast
import com.example.wingsoffireocmaker.core.extensions.startIntent
import com.example.wingsoffireocmaker.core.helper.BitmapHelper
import com.example.wingsoffireocmaker.core.helper.MediaHelper
import com.example.wingsoffireocmaker.core.utils.SaveState
import com.example.wingsoffireocmaker.core.utils.key.IntentKey
import com.example.wingsoffireocmaker.core.utils.key.ValueKey
import com.example.wingsoffireocmaker.databinding.ActivitySuccessBinding
import com.example.wingsoffireocmaker.databinding.ActivityViewBinding
import com.example.wingsoffireocmaker.ui.home.HomeActivity
import com.example.wingsoffireocmaker.ui.mycreation.MycreationActivity
import com.example.wingsoffireocmaker.ui.view.ViewViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class SuccessActivity : BaseActivity<ActivitySuccessBinding>() {
    private val viewModel: SuccessViewModel by viewModels()

    override fun setViewBinding(): ActivitySuccessBinding {
        return ActivitySuccessBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.setPath(intent.getStringExtra(IntentKey.INTENT_KEY)!!)
        viewModel.setType(intent.getIntExtra(IntentKey.TYPE_KEY, ValueKey.TYPE_VIEW))

    }
    override fun dataObservable() {

//        pathInternal
        lifecycleScope.launch {
            viewModel.pathInternal.collect { path ->
                loadImageGlide(this@SuccessActivity, path, binding.imvImage)
            }
        }

    }
    override fun viewListener() {
        binding.apply {


            btnBack.onSingleClick {
                handleBack()
            }

            btnHome.onSingleClick {
                startIntent(HomeActivity::class.java)
                finishAffinity()
            }
            btnShare.onSingleClick {
                val bitmap = createBitmap(binding.layoutCustomLayer.width, binding.layoutCustomLayer.height)
                val canvas = Canvas(bitmap)
                binding.layoutCustomLayer.draw(canvas)
                handleShare(this@SuccessActivity,bitmap)

            }
            btnDowLoad.onSingleClick {

                lifecycleScope.launch {
                    viewModel.saveImageToExternalStorage(
                        this@SuccessActivity,
                        binding.layoutCustomLayer
                    )
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
        }
    }

    private fun saveImageToAlbum() {
        binding.layoutCustomLayer.post {
            val width = binding.layoutCustomLayer.width
            val height = binding.layoutCustomLayer.height

            if (width <= 0 || height <= 0) {
                // Nếu view chưa đo xong, đo thủ công
                binding.layoutCustomLayer.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                binding.layoutCustomLayer.layout(
                    0,
                    0,
                    binding.layoutCustomLayer.measuredWidth,
                    binding.layoutCustomLayer.measuredHeight
                )
            }

            lifecycleScope.launch {
                val bitmap = BitmapHelper.createBimapFromView(binding.layoutCustomLayer)
                MediaHelper.saveBitmapToInternalStorage(
                    this@SuccessActivity,
                    ValueKey.ALBUM_BACKGROUND,
                    bitmap
                ).collect { result ->
                    when (result) {
                        is SaveState.Loading -> showLoading()
                        is SaveState.Error -> {
                            dismissLoading(true)
                            showToast(R.string.save_failed_please_try_again)
                        }
                        is SaveState.Success -> {
                            dismissLoading(true)
                            showToast(R.string.image_has_been_saved_successfully)
                        }
                    }
                }
            }
        }
    }

    override fun initText() {

    }

}