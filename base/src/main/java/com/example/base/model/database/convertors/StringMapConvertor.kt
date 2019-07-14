package com.example.base.model.database.convertors

import androidx.room.TypeConverter
import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

class StringMapConvertor {

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val map = HashMap<String, String>()
        val jsonObject = JSONObject(value)
        val keyIterator = jsonObject.keys()
        while (keyIterator.hasNext()) {
            val key = keyIterator.next()
            map[key] = jsonObject.getString(key)
        }
        return map
    }

    @TypeConverter
    fun toString(stringMap: Map<String, String>): String {
        val jsonObject = JSONObject()
        for ((key, value) in stringMap) {
            jsonObject.put(key, value)
        }
        return jsonObject.toString()
    }

}
