package com.example.wingsoffireocmaker.data.custom

import com.example.wingsoffireocmaker.data.model.BackGroundModel

data class CustomizeModel(
    val dataName: String = "",
    val avatar: String = "",
    val layerList: ArrayList<LayerListModel> = arrayListOf(),
    val backgroundList: ArrayList<BackGroundModel>? = null, // có thể null
    val selectedBackgroundPath: String? = null
)