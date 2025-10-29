package com.example.wingsoffireocmaker.ui.background

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.wingsoffireocmaker.R
import com.example.wingsoffireocmaker.core.extensions.showToast
import com.example.wingsoffireocmaker.core.helper.AssetHelper
import com.example.wingsoffireocmaker.core.helper.BitmapHelper
import com.example.wingsoffireocmaker.core.helper.InternetHelper
import com.example.wingsoffireocmaker.core.helper.MediaHelper
import com.example.wingsoffireocmaker.core.utils.DataLocal
import com.example.wingsoffireocmaker.core.utils.HandleState
import com.example.wingsoffireocmaker.core.utils.SaveState
import com.example.wingsoffireocmaker.core.utils.key.AssetsKey
import com.example.wingsoffireocmaker.core.utils.key.ValueKey
import com.example.wingsoffireocmaker.data.model.BackGroundModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackgroundViewModel : ViewModel() {
    private val _backgroundList = MutableStateFlow<ArrayList<BackGroundModel>>(arrayListOf())
    val backgroundList: StateFlow<ArrayList<BackGroundModel>> = _backgroundList.asStateFlow()

    private val _pathInternalTemp = MutableStateFlow<String>("")
    val pathInternalTemp: StateFlow<String> = _pathInternalTemp.asStateFlow()



    fun loadBackground(context: Context) {
        val list = arrayListOf<BackGroundModel>()
        list.add(BackGroundModel(path = null, isSelected = true))
        val assetList = AssetHelper.getSubfoldersAsset(context, AssetsKey.BACKGROUND_ASSET)
        assetList.forEachIndexed { index, path ->
            list.add(BackGroundModel(path = path, isSelected = false))
        }
        _backgroundList.value = list
    }
    fun changeFocusBackgroundList(position: Int) {
        _backgroundList.value = _backgroundList.value.mapIndexed { index, model ->
            model.copy(isSelected = index == position)
        }.toCollection(ArrayList())
    }

    fun setPathInternalTemp(path: String) {
        _pathInternalTemp.value = path
    }

    fun saveImageFromView(context: Context, view: View): Flow<SaveState> = flow {
        emit(SaveState.Loading)
        val bitmap = BitmapHelper.createBimapFromView(view)
        MediaHelper.saveBitmapToInternalStorage(
            context,
            ValueKey.ALBUM_BACKGROUND,
            bitmap
        ).collect { state ->
            emit(state)
        }
    }.flowOn(Dispatchers.IO)


}