package by.riewe.cadence.data.local.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Класс для конвертации сложных типов данных в форматы, понятные SQLite (и обратно).
 * Room автоматически использует эти методы при сохранении и чтении из БД.
 */
class Converters {
    
    /**
     * Конвертирует Long (Timestamp) из базы данных в объект Date для использования в коде.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Конвертирует объект Date в Long (Timestamp) для сохранения в SQLite.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
