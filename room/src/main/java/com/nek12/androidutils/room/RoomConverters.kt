package com.nek12.androidutils.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Duration
import java.time.Instant
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
private fun capped(instant: Instant): Instant {
    val instants = arrayOf(Instant.MIN, instant, Instant.MAX)
    Arrays.sort(instants)
    return instants[1]
}


/**
 * Type converters that provide utility conversions for you:
 * Duration, UUID, Instant
 */
@TypeConverters
@RequiresApi(Build.VERSION_CODES.O)
class RoomConverters {
    @TypeConverter
    fun toDuration(seconds: Long?): Duration? {
        return seconds?.let { Duration.ofSeconds(it) }
    }

    @TypeConverter
    fun fromDuration(value: Duration?): Long? {
        return value?.seconds
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(uuid) }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.let { capped(it).toEpochMilli() }
    }

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? {
        return millisSinceEpoch?.let {
            Instant.ofEpochMilli(millisSinceEpoch)
        }
    }
}
