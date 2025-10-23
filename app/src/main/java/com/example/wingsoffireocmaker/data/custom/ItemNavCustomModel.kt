package com.example.wingsoffireocmaker.data.custom

data class ItemNavCustomModel(
    val path: String,
    val positionCustom: Int,
    val positionNavigation: Int,
    var isSelected: Boolean = false,
    val listImageColor: ArrayList<ItemColorImageModel> = arrayListOf()
)