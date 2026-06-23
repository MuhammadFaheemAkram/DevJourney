package co.bitfuse.devjourney.core.database.converter

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(separator = LIST_SEPARATOR)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value
            .takeIf { it.isNotBlank() }
            ?.split(LIST_SEPARATOR)
            ?: emptyList()
    }

    private companion object {
        const val LIST_SEPARATOR = "\u001F"
    }
}
