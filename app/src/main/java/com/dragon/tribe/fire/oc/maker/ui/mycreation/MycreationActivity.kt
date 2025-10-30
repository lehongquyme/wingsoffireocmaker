package com.dragon.tribe.fire.oc.maker.ui.mycreation

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.dragon.tribe.fire.oc.maker.R
import com.dragon.tribe.fire.oc.maker.core.base.BaseActivity
import com.dragon.tribe.fire.oc.maker.core.dialog.ConfirmDialog
import com.dragon.tribe.fire.oc.maker.core.extensions.handleBack
import com.dragon.tribe.fire.oc.maker.core.extensions.hideNavigation
import com.dragon.tribe.fire.oc.maker.core.extensions.onSingleClick
import com.dragon.tribe.fire.oc.maker.core.extensions.showToast
import com.dragon.tribe.fire.oc.maker.core.extensions.startIntent
import com.dragon.tribe.fire.oc.maker.core.helper.MediaHelper
import com.dragon.tribe.fire.oc.maker.core.utils.HandleState
import com.dragon.tribe.fire.oc.maker.core.utils.SystemUtils.setLocale
import com.dragon.tribe.fire.oc.maker.core.utils.key.ValueKey.ALBUM_BACKGROUND
import com.dragon.tribe.fire.oc.maker.data.model.MyCreationModel
import com.dragon.tribe.fire.oc.maker.databinding.ActivityMycreationBinding
import com.dragon.tribe.fire.oc.maker.ui.view.ViewActivity
import kotlinx.coroutines.launch

class MycreationActivity : BaseActivity<ActivityMycreationBinding>() {
    private lateinit var adapter: MyCreationAdapter

    override fun setViewBinding(): ActivityMycreationBinding {
        return ActivityMycreationBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        binding.apply {
            txtShare.isSelected=true
            txtDownLoad.isSelected=true
        }
        adapter = MyCreationAdapter(this)
        binding.rcv.apply {
            layoutManager = GridLayoutManager(this@MycreationActivity, 2)
            adapter = this@MycreationActivity.adapter
            itemAnimator = null
        }

        loadInternalImages()
        setupAdapterClick()
    }

    override fun viewListener() {
        binding.apply {
            btnBack.onSingleClick {
                if (adapter.isSelectionMode()) {
                    clearSelectionMode()
                } else {
                    handleBack()
                }
            }

            btnShare.onSingleClick {
                shareSelectedImages()
            }

            btnDowLoad.onSingleClick {
                downloadSelectedImages()
            }

            btnDeleteTick.onSingleClick {
                confirmDelete()
            }
        }
    }

    override fun initText() {}

    private fun loadInternalImages() {
        val paths = MediaHelper.getImageInternal(this, ALBUM_BACKGROUND)
        binding.noData.visibility = if (paths.isEmpty()) View.VISIBLE else View.GONE

        val listModels = paths.map { path -> MyCreationModel(path = path) }
        adapter.submitList(ArrayList(listModels))
        updateBackIcon()
    }

    private fun setupAdapterClick() {
        adapter.onItemClick = { path ->
            val position = adapter.listMyLibrary.indexOfFirst { it.path == path }
            if (position != -1) {
                if (adapter.isSelectionMode()) {
                    // 👉 Đang ở chế độ chọn → mở ảnh và thoát chế độ chọn
                    clearSelectionMode()
                    startIntent(ViewActivity::class.java, path)
                } else {
                    // 👉 Không ở chế độ chọn → mở ảnh như thường
                    startIntent(ViewActivity::class.java, path)
                }
            } else {
                // 👉 Không tìm thấy ảnh (phòng lỗi)
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
            }
        }

        adapter.onLongClick = { position ->
            // 👉 Nhấn giữ → bật chế độ chọn
            if (!adapter.isSelectionMode()) {
                adapter.enableSelectionMode(true)
                binding.layoutBot.visibility = View.VISIBLE
            }
            adapter.submitItem(position, true)
            updateDeleteButtonVisibility()
        }

        adapter.onItemTick = { position ->
            // 👉 Tick icon → toggle chọn/bỏ chọn
            if (adapter.isSelectionMode()) {
                adapter.toggleSelect(position)
                updateDeleteButtonVisibility()
            }
        }
    }





    private fun updateDeleteButtonVisibility() {
        val count = adapter.getSelectedCount()
        if (count > 0) {
            binding.layoutBot.visibility = View.VISIBLE
        } else {

            binding.layoutBot.visibility = View.GONE
        }
        updateBackIcon()
    }

    private fun updateBackIcon() {
        if (adapter.isSelectionMode() ) {
            binding.btnBack.setImageResource(R.drawable.ic_exit)
            binding.btnDeleteTick.visibility = View.VISIBLE

        } else {
            binding.btnBack.setImageResource(R.drawable.back_language)
            binding.btnDeleteTick.visibility = View.GONE
        }
    }

    private fun confirmDelete() {
        val selectedCount = adapter.getSelectedCount()
        if (selectedCount == 0) {
            Toast.makeText(this, getString(R.string.select_imgae), Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = ConfirmDialog(this, R.string.delete, R.string.do_you_want_to_delete)
        setLocale(this)
        dialog.onYesClick = {
            deleteSelectedImages()
            dialog.dismiss()
        }
        dialog.onNoClick = {
            dialog.dismiss()
            hideNavigation()
        }
        dialog.show()
    }


    private fun deleteSelectedImages() {
        val selectedPaths = adapter.listMyLibrary.filter { it.isSelected }.map { it.path }
        if (selectedPaths.isEmpty()) return

        lifecycleScope.launch {
            MediaHelper.deleteFileByPath(ArrayList(selectedPaths)).collect { state ->
                when (state) {
                    HandleState.LOADING -> showLoading()
                    HandleState.SUCCESS -> {
                        dismissLoading(true)
                        loadInternalImages()
                        clearSelectionMode()
                        showToast(R.string.delete_success)
                    }
                    else -> {
                        dismissLoading(true)
                        Toast.makeText(
                            this@MycreationActivity,
                            "Xóa thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun shareSelectedImages() {
        val selectedPaths = adapter.listMyLibrary.filter { it.isSelected }.map { it.path }
        if (selectedPaths.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 ảnh để chia sẻ", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uris = ArrayList<android.net.Uri>()
            for (path in selectedPaths) {
                val file = java.io.File(path)
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                uris.add(uri)
            }

            val intent = android.content.Intent(android.content.Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, uris)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(android.content.Intent.createChooser(intent, "Chia sẻ ảnh bằng..."))
            clearSelectionMode()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Không thể chia sẻ ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadSelectedImages() {
        val selectedPaths = adapter.listMyLibrary.filter { it.isSelected }.map { it.path }
        if (selectedPaths.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 ảnh để tải xuống", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            MediaHelper.downloadPartsToExternal(this@MycreationActivity, selectedPaths).collect { state ->
                when (state) {
                    HandleState.LOADING -> showLoading()
                    HandleState.SUCCESS -> {
                        dismissLoading(true)
                        Toast.makeText(
                            this@MycreationActivity,
                            "Đã tải ${selectedPaths.size} ảnh vào thư viện",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearSelectionMode()
                    }
                    else -> {
                        dismissLoading(true)
                        Toast.makeText(
                            this@MycreationActivity,
                            "Tải ảnh thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadInternalImages()
    }

    private fun clearSelectionMode() {
        adapter.clearAllSelections()
        adapter.enableSelectionMode(false)
        binding.layoutBot.visibility = View.GONE
        updateBackIcon()
    }
}
