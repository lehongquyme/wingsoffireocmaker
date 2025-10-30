package com.dragon.tribe.fire.oc.maker.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dragon.tribe.fire.oc.maker.core.base.BaseActivity
import com.dragon.tribe.fire.oc.maker.core.utils.SystemUtils
import com.dragon.tribe.fire.oc.maker.ui.home.DataViewModel
import com.dragon.tribe.fire.oc.maker.R
import com.dragon.tribe.fire.oc.maker.databinding.ActivitySplashBinding
import com.dragon.tribe.fire.oc.maker.ui.intro.IntroActivity
import com.dragon.tribe.fire.oc.maker.ui.language.LanguageActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var check = false
    private val viewModel: DataViewModel by viewModels()
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(
                Intent.ACTION_MAIN
            )
        ) {
            finish(); return;
        }

        viewModel.ensureData(this)

    }

    override fun dataObservable() {
        lifecycleScope.launch {
            viewModel.allData.collect { data ->
                if (data.isNotEmpty()){
                    delay(3000)
                    if (SystemUtils.isFirstLang(this@SplashActivity)) {
                        startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
                        check = true
                        finishAffinity()
                    } else {
                        startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                        check = true
                        finishAffinity()
                    }
                }
            }
        }
    }
    override fun viewListener() {

    }

    override fun initText() {

    }

    override fun onBackPressed() {
        if (check) {
            super.onBackPressed()
        } else {
            check = false
        }
    }

}