package com.example.tasky.agenda.data.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> getListOfItemAdapter() = object : ColumnAdapter<List<T>, String> {
    private val DELIMITER = "|"

    override fun decode(databaseValue: String): List<T> {
        return if (databaseValue.isEmpty()) {
            emptyList()
        } else {
            val stringsList = databaseValue.split(DELIMITER)
            stringsList.map {
                Json.decodeFromString(it)
            }
        }
    }

    override fun encode(value: List<T>) = run {
        val encodedList = value.map { Json.encodeToString(it) }
        encodedList.joinToString(separator = DELIMITER)
    }
}