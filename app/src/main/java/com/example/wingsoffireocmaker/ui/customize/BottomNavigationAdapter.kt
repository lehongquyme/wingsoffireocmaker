package com.example.wingsoffireocmaker.ui.customize

import android.content.Context
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.wingsoffireocmaker.core.base.BaseAdapter
import com.example.wingsoffireocmaker.core.extensions.onSingleClick
import com.example.wingsoffireocmaker.core.utils.DataLocal
import com.example.wingsoffireocmaker.data.custom.NavigationModel
import com.example.wingsoffireocmaker.databinding.ItemNaviBinding
import com.facebook.shimmer.ShimmerDrawable


class BottomNavigationAdapter(val context: Context) :
    BaseAdapter<NavigationModel, ItemNaviBinding>(ItemNaviBinding::inflate) {
    var onItemClick: ((Int) -> Unit) = {}
    override fun onBind(
        binding: ItemNaviBinding, item: NavigationModel, position: Int
    ) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(DataLocal.shimmer)
        }
        binding.apply {
            vFocus.isVisible = item.isSelected
            Glide.with(root).load(item.imageNavigation).placeholder(shimmerDrawable).into(imvImage)
            root.onSingleClick { onItemClick.invoke(position) }
        }
    }
}