package com.dragon.tribe.fire.oc.maker.ui.view

import android.app.Activity
import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import com.dragon.tribe.fire.oc.maker.core.helper.MediaHelper
import com.dragon.tribe.fire.oc.maker.core.utils.HandleState
import com.dragon.tribe.fire.oc.maker.core.utils.SaveState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ViewViewModel : ViewModel(){
    fun saveImageToExternalStorage(activity: Activity, targetView: View): Flow<SaveState> = flow {
        emit(SaveState.Loading)

        try {
            val bitmap = createBitmap(targetView.width, targetView.height)
            val canvas = Canvas(bitmap)
            targetView.draw(canvas)

            MediaHelper.saveBitmapToExternal(activity, bitmap).collect { handle ->
                when (handle) {
                    HandleState.LOADING -> {}
                    HandleState.SUCCESS -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(activity, "Đã lưu ảnh vào thư viện!", Toast.LENGTH_SHORT).show()
                        }
                        emit(SaveState.Success(path = ""))
                    }

                    HandleState.FAIL -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(activity, "Lưu thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                        }
                        emit(SaveState.Error(Exception("Save failed")))
                    }

                    else -> Unit
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "Đã xảy ra lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            emit(SaveState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}