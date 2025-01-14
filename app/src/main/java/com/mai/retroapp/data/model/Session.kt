package com.mai.retroapp.data.model

data class Session(
    var id: String = "",
    var name: String = "",
    var password: String = "",
    var cards: Map<String, Card> = HashMap(),
    var showUsername: Boolean = false
) {
    constructor() : this("", "", "", HashMap())
}