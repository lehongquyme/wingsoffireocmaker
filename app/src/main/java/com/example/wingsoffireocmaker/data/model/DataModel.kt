package com.example.wingsoffireocmaker.data.model

import com.example.wingsoffireocmaker.data.custom.ItemColorModel
import com.example.wingsoffireocmaker.data.custom.ItemNavCustomModel

data class DataModel(
    val avatarId: String,
    var pathInternal: String? = null,
    val isFlip: Boolean = false,
    val listItemNav: ArrayList<ArrayList<ItemNavCustomModel>> = arrayListOf(),
    val listColorItemNav: ArrayList<ArrayList<ItemColorModel>> = arrayListOf(),
    val listPartSelected: ArrayList<String> = arrayListOf(), // danh sách imageNavigation của từng layer
    val listKeySelectedItem: ArrayList<String> = arrayListOf(),
    val listIsSelectedItem: ArrayList<Boolean> = arrayListOf(),
    val listPositionColorItem: ArrayList<Int> = arrayListOf(),
    val positionNavSelected: Int = 0, // thêm: tab đang chọn
    var selectedBackground: Int? =0,
    var backgroundPath: String? = null

)
