package com.example.base.model.database.convertors

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber

import java.util.ArrayList

class StringListConvertor {

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val list = ArrayList<String>()
        val jsonArray = JSONArray(value)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        val jsonArray = JSONArray()
        for (string in stringList) {
            jsonArray.put(string)
        }
        return jsonArray.toString()
    }

}
