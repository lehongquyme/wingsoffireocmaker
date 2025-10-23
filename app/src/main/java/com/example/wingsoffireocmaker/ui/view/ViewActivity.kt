package com.example.wingsoffireocmaker.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.databinding.ActivityViewBinding

class ViewActivity : BaseActivity<ActivityViewBinding>() {
    override fun setViewBinding(): ActivityViewBinding {
        return ActivityViewBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {

    }

    override fun viewListener() {

    }

    override fun initText() {

    }

}