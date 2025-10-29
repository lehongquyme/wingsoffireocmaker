package com.example.wingsoffireocmaker.ui.language

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.Toast
import com.example.wingsoffireocmaker.core.base.BaseActivity
import com.example.wingsoffireocmaker.core.extensions.handleBack
import com.example.wingsoffireocmaker.core.extensions.hide
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.extensions.select
import com.example.wingsoffireocmaker.core.extensions.show
import com.example.wingsoffireocmaker.core.extensions.startIntentAnim
import com.example.wingsoffireocmaker.core.utils.DataLocal.getLanguageList
import com.example.wingsoffireocmaker.core.utils.KeyApp.INTENT_KEY
import com.example.wingsoffireocmaker.core.utils.SystemUtils
import com.example.wingsoffireocmaker.core.utils.SystemUtils.setFirstLang
import com.example.wingsoffireocmaker.core.utils.SystemUtils.setPreLanguage
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.databinding.ActivityLanguageBinding
import com.example.wingsoffireocmaker.ui.intro.IntroActivity
import com.example.wingsoffireocmaker.data.model.LanguageModel
import com.example.wingsoffireocmaker.ui.home.HomeActivity
import kotlin.collections.forEach
import kotlin.system.exitProcess
class LanguageActivity  : BaseActivity<ActivityLanguageBinding>() {
    private var listLanguage: ArrayList<LanguageModel> = arrayListOf()
    private var codeLang: String = ""
    private var isFirstAccess: Boolean = true
    private val languageAdapter by lazy {
        LanguageAdapter(this)
    }
    override fun setViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initData()
        initRcv()
    }

    override fun viewListener() {
        binding.apply {
            btnBack.onSingleClick{
                handleBack()
            }
            btnDone.onSingleClick {
                handleDoneFirst()
            }
            btnChangSetting.onSingleClick {
                handleChangeSetting()
            }
            handleRcv()
            txtLanguageStart.select()
            txtLanguageCenter.select()
        }
    }

    override fun initText() {

    }
    private fun initData(){
        binding.apply {
            val intent = intent.getStringExtra(INTENT_KEY)
            if(intent != null){
                isFirstAccess = false
                codeLang = SystemUtils.getPreLanguage(this@LanguageActivity)
                txtLanguageCenter.show()
                btnDone.hide()
                btnBack.show()
                btnChangSetting.hide()
            }else{
                isFirstAccess = true
                codeLang = SystemUtils.getPreLanguage(this@LanguageActivity)
                txtLanguageCenter.show()
                btnDone.show()
                btnBack.hide()
                btnChangSetting.hide()
            }

            if(!SystemUtils.isFirstLang(this@LanguageActivity)){
                btnDone.hide()
                txtLanguageCenter.show()
                btnBack.show()
                btnChangSetting.show()
                txtLanguageStart.hide()
            }else{
                btnDone.show()
                txtLanguageCenter.show()
                btnChangSetting.hide()
            }
        }
    }

    private fun initRcv(){
        binding.apply {

            listLanguage.clear()
            listLanguage.addAll(getLanguageList())

            val lang = SystemUtils.getPreLanguage(this@LanguageActivity)

            for(i in listLanguage.indices){
                if(listLanguage[i].code == lang){
                    val matchingLanguage = listLanguage[i]
                    listLanguage.removeAt(i)
                    listLanguage.add(0,matchingLanguage)

                    if(!SystemUtils.isFirstLang(this@LanguageActivity)){
                        listLanguage[0].activate = true
                    }
                    break
                }
            }
            if(isFirstAccess){
                codeLang = ""
                listLanguage.forEach { it.activate = false }
            }
            rcv.adapter = languageAdapter
            rcv.itemAnimator = null
            languageAdapter.submitList(listLanguage)


        }
        languageAdapter.onItemClick = { selected ->
            codeLang = selected.code
            if (isFirstAccess) {
                handleDoneFirst()
            }
        }
    }

    private fun handleDoneFirst(){
        if(codeLang == ""){
            Toast.makeText(this, R.string.not_select_lang, Toast.LENGTH_SHORT).show()
        }else{
            setPreLanguage(this@LanguageActivity, codeLang)
            setFirstLang(this,false)

            startIntentAnim(IntroActivity::class.java)
            finishAffinity()
        }
    }

    private fun handleChangeSetting(){
        setPreLanguage(this@LanguageActivity, codeLang)
        startIntentAnim(HomeActivity::class.java)
        finishAffinity()
    }

    private fun handleRcv(){
        binding.apply {
            languageAdapter.onItemClick = {
                    item -> codeLang = item.code
                languageAdapter.submitItem(item.code)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(!SystemUtils.isFirstLang(this)){
            handleBack()
        }else{
            exitProcess(0)
        }
    }

}