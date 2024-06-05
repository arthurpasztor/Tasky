package com.example.tasky.main.data.dto

interface AgendaListItem {
    fun getTimestamp(): Long

    fun getItemTitle(): String = ""

    fun getItemDescription(): String = ""

    fun getFormattedTime() : String = ""

    fun isItemDone(): Boolean = false
}

class NeedleItem: AgendaListItem {
    override fun getTimestamp() = System.currentTimeMillis()
}