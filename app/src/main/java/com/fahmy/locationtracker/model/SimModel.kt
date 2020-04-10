package com.fahmy.locationtracker.model

class CellLocationModel {
    val token = "ff34cd2e677a8f"
    val radio = "umts"
    var mcc: Int? = null
    var mnc: Int? = null
    var cells: ArrayList<CellsEntity>? = null
    val address = 1
}

data class CellsEntity(
    var lac: Int? = null,
    var cid: Int? = null,
    var psc: Int = 0
)