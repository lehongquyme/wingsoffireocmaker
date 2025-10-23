package com.example.wingsoffireocmaker.ui.setting

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.dialog.RateDialog
import com.example.wingsoffireocmaker.core.extensions.handleBack
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.openPlayStoreForReview
import com.example.wingsoffireocmaker.core.extensions.showToast
import com.example.wingsoffireocmaker.core.extensions.startIntentAnim
import com.example.wingsoffireocmaker.core.utils.KeyApp.FROM_SETTINGS
import com.example.wingsoffireocmaker.core.utils.KeyApp.INTENT_KEY
import com.example.wingsoffireocmaker.core.utils.SystemUtils.policy
import com.example.wingsoffireocmaker.core.utils.SystemUtils.shareApp
import com.example.wingsoffireocmaker.databinding.ActivitySettingBinding
import com.example.wingsoffireocmaker.ui.language.LanguageActivity

class SettingActivity  : BaseActivity<ActivitySettingBinding>() {
    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {

    }

    override fun viewListener() {
        binding.apply {
            btnBack.onSingleClick{
                handleBack()
            }
            btnLang.onSingleClick {
                startIntentAnim(LanguageActivity::class.java,FROM_SETTINGS)
            }
            btnRate.onSingleClick {
                val rateDialog = RateDialog(this@SettingActivity)
                rateDialog.init(object : RateDialog.OnPress {
                    override fun send(rate: Float) {
                        binding.btnRate.visibility = View.GONE
                        if (rate>3L)
                            openPlayStoreForReview(this@SettingActivity)
                        else
                            showToast(R.string.have_rated)
                        rateDialog.dismiss()

                    }

                    override fun rating() {
                    }

                    override fun cancel() {
                    }

                    override fun later() {
                    }

                })
                rateDialog.show()
            }

            btnShare.onSingleClick {
                shareApp()
            }
            btnPolicy.onSingleClick {
                policy()
            }
        }

    }

    override fun initText() {

    }

}