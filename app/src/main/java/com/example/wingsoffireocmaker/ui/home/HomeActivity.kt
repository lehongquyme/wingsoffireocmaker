package com.example.wingsoffireocmaker.ui.home

import SuggestionAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.dialog.ConfirmDialog
import com.example.wingsoffireocmaker.core.dialog.RateDialog
import com.example.wingsoffireocmaker.core.extensions.dLog
import com.example.wingsoffireocmaker.core.extensions.dLogQ
import com.example.wingsoffireocmaker.core.extensions.eLog
import com.example.wingsoffireocmaker.core.extensions.hideNavigation
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.openPlayStoreForReview
import com.example.wingsoffireocmaker.core.extensions.showToast
import com.example.wingsoffireocmaker.core.extensions.startIntent
import com.example.wingsoffireocmaker.core.extensions.startIntentAnim
import com.example.wingsoffireocmaker.core.helper.MediaHelper
import com.example.wingsoffireocmaker.core.utils.KeyApp.INTENT_KEY
import com.example.wingsoffireocmaker.core.utils.SystemUtils
import com.example.wingsoffireocmaker.core.utils.SystemUtils.isCountBack
import com.example.wingsoffireocmaker.core.utils.SystemUtils.setCountBack
import com.example.wingsoffireocmaker.core.utils.key.IntentKey
import com.example.wingsoffireocmaker.core.utils.key.IntentKey.FROM_INTRO
import com.example.wingsoffireocmaker.core.utils.key.ValueKey
import com.example.wingsoffireocmaker.data.model.SuggestionModel
import com.example.wingsoffireocmaker.databinding.ActivityHomeBinding
import com.example.wingsoffireocmaker.ui.category.CategoryActivity
import com.example.wingsoffireocmaker.ui.customize.CustomizeActivity
import com.example.wingsoffireocmaker.ui.customize.CustomizeViewModel
import com.example.wingsoffireocmaker.ui.home.suggestionviewmodel.SuggestionViewModel
import com.example.wingsoffireocmaker.ui.mycreation.MycreationActivity
import com.example.wingsoffireocmaker.ui.setting.SettingActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private var initJob: Job? = null
    private var countHome: Int = 0
    private val suggestionViewModel: SuggestionViewModel by viewModels()
    private val suggestionAdapter by lazy { SuggestionAdapter(this)}
    private val dataViewModel: DataViewModel by viewModels()
    private val customizeViewModel: CustomizeViewModel by viewModels()



    override fun setViewBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

    override fun initView() {
        onBackPressedDispatcher.addCallback(this) {
            setupBackPressHandler()}
        val from = intent.getStringExtra(INTENT_KEY)
        if (from == FROM_INTRO) {
            countHome = isCountBack(this) + 1
            setCountBack(this, countHome)
        }
        dataViewModel.ensureData(this)
    }

    override fun viewListener() {
        binding.apply {
            btnWing.onSingleClick { startIntentAnim(CategoryActivity::class.java) }
            btnMyCreation.onSingleClick { startIntentAnim(MycreationActivity::class.java) }
            btnSetting.onSingleClick { startIntentAnim(SettingActivity::class.java) }
        }
        suggestionAdapter.onItemClick = { model -> handleItemClick(model)}

    }
    private fun initData() {
        val handleExceptionCoroutine = CoroutineExceptionHandler { _, throwable ->
            eLog("initData: ${throwable.message}")
            CoroutineScope(Dispatchers.Main).launch {
                dismissLoading(true)
                val dialogExit = ConfirmDialog(this@HomeActivity, R.string.error, R.string.an_error_occurred)
                dialogExit.show()
                dialogExit.onNoClick = { dialogExit.dismiss(); finish() }
                dialogExit.onYesClick = {
                    dialogExit.dismiss()
                    hideNavigation()
                    startIntent(HomeActivity::class.java, customizeViewModel.positionSelected)
                    finish()
                }
            }
        }
        initJob?.cancel()

        initJob =CoroutineScope(SupervisorJob() + Dispatchers.IO + handleExceptionCoroutine).launch {
            showLoading()
            // Prepare data
            val deferred1 = async {
                customizeViewModel.resetDataList()
                customizeViewModel.addValueToItemNavList()
                customizeViewModel.setItemColorDefault()
                customizeViewModel.setBottomNavigationListDefault()
                true
            }
            val deferred2 = async {
                if (deferred1.await()) {
                    repeat(ValueKey.RANDOM_QUANTITY) {
                        customizeViewModel.setClickRandomFullLayer()
                        suggestionViewModel.updateRandomList(customizeViewModel.getSuggestionList())
                    }
                    repeat(ValueKey.RANDOM_QUANTITY) {
                        customizeViewModel.setClickRandomFullLayer()
                        suggestionViewModel.updateRandomList(customizeViewModel.getSuggestionList())
                    }

                    suggestionViewModel.randomList.forEach { s ->
                        s.backgroundList?.takeIf { it.isNotEmpty() }?.let { list ->
                            val bg = list.random()
                            s.randomBackgroundPath = bg.path
                            Log.d("BG_DEBUG", "Saved path = ${s.randomBackgroundPath}")
                        } ?: Log.d("BG_DEBUG", "No background list for suggestion.")
                    }

                }
                true
            }

            withContext(Dispatchers.Main) {
                if (deferred1.await() && deferred2.await()) {
                    initRcv()

                    var isDismissed = false

                    suggestionAdapter.onAllItemsLoaded = {
                        if (!isDismissed) {
                            isDismissed = true
                            lifecycleScope.launch(Dispatchers.Main) {
                                dismissLoading()
                            }
                        }
                    }


                    // ⏱ Timeout fallback (5 giây)
                    lifecycleScope.launch {
                        delay(5000)
                        if (!isDismissed) {
                            Log.d("BG_DEBUG", "Timeout dismiss loading")
                            isDismissed = true
                            dismissLoading()
                        }
                    }

                    suggestionAdapter.setList(ArrayList(suggestionViewModel.randomList))
                }
            }
        }
    }


    override fun dataObservable() {
        lifecycleScope.launch {
            dataViewModel.allData.collect { data ->
                dLogQ("STEP 1: allData size = ${data.size}")

                if (data.isNotEmpty()) {
                    val pos = intent.getIntExtra(IntentKey.INTENT_KEY, 0)
                    customizeViewModel.positionSelected = pos
                    customizeViewModel.setDataCustomize(data[pos])  // <--- Cần dòng này trước
                    customizeViewModel.setIsDataAPI(pos >= ValueKey.POSITION_API)
                    customizeViewModel.updateAvatarPath(customizeViewModel.dataCustomize.value!!.avatar)
                    dLogQ("STEP 2: dataCustomize = ${customizeViewModel.dataCustomize.value?.dataName}")
                    customizeViewModel.loadBackgroundData(this@HomeActivity)
                    // Sau khi đã có dataCustomize, mới gọi initData()
                    initData()
                }
            }
        }
    }

    override fun initText() {}

    private fun setupBackPressHandler() {
        val currentCount = isCountBack(this@HomeActivity)
        if (currentCount % 2 == 0) {
            val rateDialog = RateDialog(this@HomeActivity)
            rateDialog.init(object : RateDialog.OnPress {
                override fun send(rate: Float) {
                    if (rate >= 4f) {
                        openPlayStoreForReview(this@HomeActivity)
                    } else {
                        showToast(R.string.have_rated)
                    }
                    SystemUtils.setRate(this@HomeActivity, true)
                    rateDialog.dismiss()
                    finish()
                }

                override fun rating() {}
                override fun cancel() {
                    rateDialog.dismiss()
                    finish()
                }

                override fun later() {
                    rateDialog.dismiss()
                    finish()
                }
            })
            rateDialog.show()
        } else {
            super.onBackPressed()
        }
    }



    override fun onBackPressed() {
        setupBackPressHandler()
    }
    private fun initRcv() {
        binding.recyclerSticker.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.HORIZONTAL, // 👈 hướng ngang
                false
            )
            adapter = suggestionAdapter
            itemAnimator = null
            visibility = View.VISIBLE // đảm bảo hiển thị
        }

        suggestionAdapter.submitList(ArrayList(suggestionViewModel.randomList))
    }


    ///quylh
    private fun handleItemClick(model: SuggestionModel) {
        customizeViewModel.checkDataInternet(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                MediaHelper.writeListToFile(
                    this@HomeActivity,
                    ValueKey.SUGGESTION_FILE_INTERNAL,
                    arrayListOf(model)
                )

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@HomeActivity, CustomizeActivity::class.java).apply {
                        putExtra(IntentKey.INTENT_KEY, customizeViewModel.positionSelected)
                        putExtra(IntentKey.STATUS_FROM_KEY, ValueKey.SUGGESTION)
                    }

                    startActivity(intent)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        initJob?.cancel()
    }


}
