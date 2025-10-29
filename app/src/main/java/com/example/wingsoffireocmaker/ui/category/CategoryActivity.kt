package com.example.wingsoffireocmaker.ui.category

import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.extensions.handleBack
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.startIntent
import com.example.wingsoffireocmaker.core.extensions.startIntentDataAnim
import com.example.wingsoffireocmaker.core.extensions.startIntentRightToLeft
import com.example.wingsoffireocmaker.core.helper.InternetHelper
import com.example.wingsoffireocmaker.core.utils.key.IntentKey.FROM_CATEGORY
import com.example.wingsoffireocmaker.core.utils.key.IntentKey.STATUS_FROM_KEY
import com.example.wingsoffireocmaker.core.utils.key.ValueKey
import com.example.wingsoffireocmaker.databinding.ActivityCategoryBinding
import com.example.wingsoffireocmaker.ui.customize.CustomizeActivity
import com.example.wingsoffireocmaker.ui.home.DataViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryActivity : BaseActivity<ActivityCategoryBinding>() {
    private val dataViewModel: DataViewModel by viewModels()
    private val avatarAdapter by lazy { CategoryAdapter(this) }
    override fun setViewBinding():ActivityCategoryBinding {
        return ActivityCategoryBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initRcv()
        lifecycleScope.launch {
            showLoading()
            delay(300)
            dataViewModel.ensureData(this@CategoryActivity)
        }
    }
    override fun dataObservable() {
        lifecycleScope.launch {
            dataViewModel.allData.collect { list ->
                if (list.isNotEmpty()) {
                    dismissLoading()
                    avatarAdapter.submitList(list)
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            btnBack.onSingleClick {
                handleBack()
            }
            swipeRefreshLayout.setOnRefreshListener {
                refreshData()
            }
        }
        handleRcv()
    }

    override fun initText() {

    }

    private fun initRcv() {
        binding.apply {
            rcv.adapter = avatarAdapter
            rcv.itemAnimator = null
        }
    }
    private fun handleRcv(){
        binding.apply {
            avatarAdapter.onItemClick = { path, position ->
//                val customizeData = dataViewModel.allData.value[position]
                startIntentRightToLeft(CustomizeActivity::class.java, position)

            }

        }
    }

    private fun refreshData(){
        if (dataViewModel.allData.value.size < ValueKey.POSITION_API && InternetHelper.checkInternet(this)){
            lifecycleScope.launch {
                showLoading()
                delay(300)
                dataViewModel.ensureData(this@CategoryActivity)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }else{
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

}