package com.bcntransit.app.model

data class FavoriteDto(
    val type: String,
    val station_code: String,
    val station_name: String,
    val station_group_code: String?,
    val line_name: String?,
    val line_name_with_emoji: String?,
    val line_code: String,
    val coordinates: List<Double>
)