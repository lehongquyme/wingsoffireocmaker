package com.dragon.tribe.fire.oc.maker.ui.success

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dragon.tribe.fire.oc.maker.R
import com.dragon.tribe.fire.oc.maker.core.base.BaseActivity
import com.dragon.tribe.fire.oc.maker.core.extensions.handleBack
import com.dragon.tribe.fire.oc.maker.core.extensions.handleShare
import com.dragon.tribe.fire.oc.maker.core.extensions.loadImageGlide
import com.dragon.tribe.fire.oc.maker.core.extensions.onSingleClick
import com.dragon.tribe.fire.oc.maker.core.extensions.showToast
import com.dragon.tribe.fire.oc.maker.core.extensions.startIntent
import com.dragon.tribe.fire.oc.maker.core.helper.BitmapHelper
import com.dragon.tribe.fire.oc.maker.core.helper.MediaHelper
import com.dragon.tribe.fire.oc.maker.core.utils.SaveState
import com.dragon.tribe.fire.oc.maker.core.utils.key.IntentKey
import com.dragon.tribe.fire.oc.maker.core.utils.key.ValueKey
import com.dragon.tribe.fire.oc.maker.databinding.ActivitySuccessBinding
import com.dragon.tribe.fire.oc.maker.databinding.ActivityViewBinding
import com.dragon.tribe.fire.oc.maker.ui.home.HomeActivity
import com.dragon.tribe.fire.oc.maker.ui.mycreation.MycreationActivity
import com.dragon.tribe.fire.oc.maker.ui.view.ViewViewModel
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
        binding.apply {
            txtShare.isSelected=true
            txtDownLoad.isSelected=true
        }
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