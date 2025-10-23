package com.example.wingsoffireocmaker.data.model

data class PartAPI(
    val position: String,
    val parts: String,
    val colorArray: String,
    val quantity: Int
)

data class DataAPI(val name: String, val parts: List<PartAPI>)