package com.kostas.kostasapp.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HeroTypeConverters {

    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    @JvmStatic
    fun fromStringList(list: List<String>?): String =
        gson.toJson(list ?: emptyList<String>(), listType)

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrBlank()) emptyList()
        else runCatching {
            gson.fromJson<List<String>>(value, listType)
        }.getOrDefault(emptyList())
}