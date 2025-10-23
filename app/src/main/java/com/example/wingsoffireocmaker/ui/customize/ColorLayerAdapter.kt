package com.example.wingsoffireocmaker.ui.customize

import android.content.Context
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.example.wingsoffireocmaker.core.base.BaseAdapter
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.data.custom.ItemColorModel
import com.example.wingsoffireocmaker.databinding.ItemColorBinding


class ColorLayerAdapter(val context: Context) :
    BaseAdapter<ItemColorModel, ItemColorBinding>(ItemColorBinding::inflate) {
    var onItemClick: ((Int) -> Unit) = {}
    override fun onBind(binding: ItemColorBinding, item: ItemColorModel, position: Int) {
        binding.apply {
            imvImage.setBackgroundColor(item.color.toColorInt())
            layoutFocus.isVisible = item.isSelected
            root.onSingleClick { onItemClick.invoke(position) }
        }
    }
}