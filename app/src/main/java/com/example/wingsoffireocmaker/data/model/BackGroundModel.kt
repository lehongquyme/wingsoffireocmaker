package com.example.wingsoffireocmaker.data.model

data class BackGroundModel(
        val path: String,
        var isSelected: Boolean= false,
        val type: BgType = BgType.NORMAL
)
enum class BgType {
    NONE, NORMAL
}
