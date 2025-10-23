package com.example.wingsoffireocmaker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.startIntentAnim
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.databinding.ActivityHomeBinding
import com.example.wingsoffireocmaker.ui.category.CategoryActivity
import com.example.wingsoffireocmaker.ui.lissticker.ListStickerActivity
import com.example.wingsoffireocmaker.ui.mycreation.MycreationActivity
import com.example.wingsoffireocmaker.ui.setting.SettingActivity

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun setViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {

    }

    override fun viewListener() {
        binding.apply {
            btnCreate.onSingleClick {
                startIntentAnim(CategoryActivity::class.java)
            }
            btnSetting.onSingleClick {
                startIntentAnim(SettingActivity::class.java)
            }

            btnMyCreation.onSingleClick {
                startIntentAnim(MycreationActivity::class.java)
            }
            binding.recyclerSticker.layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

        }
    }

    override fun initText() {

    }

}