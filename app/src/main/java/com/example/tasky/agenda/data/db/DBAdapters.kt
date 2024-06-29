package com.example.tasky.agenda.data.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> getListOfItemAdapter() = object : ColumnAdapter<List<T>, String> {

    override fun decode(databaseValue: String): List<T> {
        return if (databaseValue.isEmpty()) {
            emptyList()
        } else {
            Json.decodeFromString<List<T>>(databaseValue)
        }
    }

    override fun encode(value: List<T>) = Json.encodeToString(value)
}