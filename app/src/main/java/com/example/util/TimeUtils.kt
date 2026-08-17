package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtils {

    /**
     * Formats raw time string from API or falls back to ID timestamp if time is "0", empty, or invalid.
     */
    fun formatDisplayTime(rawTime: String?, idTimestamp: String?): String {
        val trimmed = rawTime?.trim()

        // If rawTime is valid and not "0", "null", or empty
        if (!trimmed.isNullOrBlank() && trimmed != "0" && trimmed != "null" && trimmed != "0000-00-00 00:00:00" && trimmed.length > 2) {
            val formats = listOf(
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss",
                "HH:mm:ss",
                "HH:mm"
            )

            for (fmt in formats) {
                try {
                    val sdfInput = SimpleDateFormat(fmt, Locale.ENGLISH)
                    val date = sdfInput.parse(trimmed)
                    if (date != null) {
                        val outputPattern = if (fmt.startsWith("HH")) "hh:mm a" else "yyyy/MM/dd hh:mm a"
                        val sdfOutput = SimpleDateFormat(outputPattern, Locale("ar", "EG"))
                        return sdfOutput.format(date)
                    }
                } catch (ignored: Exception) {
                }
            }

            // If it's already a readable string, return it
            return trimmed
        }

        // If rawTime is "0" or invalid, try parsing ID as Unix millisecond timestamp
        if (!idTimestamp.isNullOrBlank()) {
            val ts = idTimestamp.trim().toLongOrNull()
            if (ts != null) {
                val date = if (ts > 1000000000000L) { // Milliseconds
                    Date(ts)
                } else if (ts > 1000000000L) { // Seconds
                    Date(ts * 1000L)
                } else {
                    null
                }

                if (date != null) {
                    val sdfOutput = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale("ar", "EG"))
                    return sdfOutput.format(date)
                }
            }
        }

        // If rawTime was "0" or blank and couldn't parse ID, return formatted current time
        val fallbackSdf = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale("ar", "EG"))
        return fallbackSdf.format(Date())
    }
}
