package com.example.wingsoffireocmaker.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.extensions.startIntentAnim
import com.example.wingsoffireocmaker.core.utils.DataLocal
import com.example.wingsoffireocmaker.core.utils.SystemUtils
import com.example.wingsoffireocmaker.core.utils.key.IntentKey.FROM_INTRO
import com.example.wingsoffireocmaker.databinding.ActivityIntroBinding
import com.example.wingsoffireocmaker.ui.home.HomeActivity
import com.example.wingsoffireocmaker.ui.permission.PermissionActivity
import kotlin.jvm.java
import kotlin.system.exitProcess

class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private var checkStarHome = false
    private val adapter = IntroAdapter(this, DataLocal.itemIntroList)
    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initVpg()
    }

    override fun viewListener() {
        binding.txtNext.setOnClickListener {
            handleNext()
        }
    }

    override fun initText() {

    }
    private fun initVpg() {
        binding.apply {
            binding.vpgTutorial.adapter = adapter
            binding.dotsIndicator.setViewPager2(binding.vpgTutorial)

        }
    }
    private fun handleNext(){
        binding.apply {
            val nextItem = binding.vpgTutorial.currentItem + 1
            if (nextItem < DataLocal.itemIntroList.size) {
                vpgTutorial.setCurrentItem(nextItem, true)
            } else {
                if (!checkStarHome) {
                    if (SystemUtils.getFirstPermission(this@IntroActivity)) {
                        checkStarHome = true
                        startIntentAnim(PermissionActivity::class.java)
                        finishAffinity()
                    } else {
                        checkStarHome = true
                        startIntentAnim(HomeActivity::class.java,FROM_INTRO)
                        finishAffinity()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        exitProcess(0)
    }

}