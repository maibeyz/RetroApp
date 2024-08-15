package com.mai.retroapp.data.model


data class Card(
    var id: String = "",
    val content: String = "",
    val type: String = "",
    var backgroundColor: String = "",
    var username: String = ""
) {
    constructor() : this("", "", "", "", "")
}