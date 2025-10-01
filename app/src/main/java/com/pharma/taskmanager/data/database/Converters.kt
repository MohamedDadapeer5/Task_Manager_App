package com.pharma.taskmanager.data.database

import androidx.room.TypeConverter

class Converters {
    
    // Since we're using Long for timestamps, we don't need any type converters
    // Room can handle Long, String, and Int natively
    
    // If you need to add custom type converters in the future, add them here
    // For example, if you want to store lists or custom objects:
    
    /*
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
    */
}