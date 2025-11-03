package com.rpl.fintrack.util

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object dateUtils {
    fun getCurrentDate(): Timestamp{
        val date = Timestamp(System.currentTimeMillis())
        return date
    }
    fun formatTimestampToDate(timestamp: Timestamp): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(timestamp)
    }
    fun subtractOneDay(timestamp: Timestamp): Timestamp{
        val oneDayMilis = 24 * 60 * 60 * 1000
        val newDate = timestamp.time - oneDayMilis
        return Timestamp(newDate)
    }
    fun addOneDay(timestamp: Timestamp): Timestamp{
        val oneDayMilis = 24 * 60 * 60 * 1000
        val newDate = timestamp.time + oneDayMilis
        return Timestamp(newDate)
    }
    fun getDate(timestamp: Timestamp): String{
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(timestamp)
    }
    @SuppressLint("NewApi")
    fun getDateString(timestamp: String): String{
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val localDateTime = LocalDateTime.parse(timestamp, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:mm a", Locale.getDefault())
            localDateTime.format(outputFormatter)
        }
        catch (e: Exception) {
            timestamp
        }
    }
    @SuppressLint("NewApi")
    fun parseDisplayDateToTimestamp(dateString: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:mm a", Locale.getDefault())
            val localDateTime = LocalDateTime.parse(dateString, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            localDateTime.format(outputFormatter)
        }
        catch (e: Exception) {
            dateString
        }
    }
    @SuppressLint("NewApi")
    fun utcToLocalString(utcString: String): String{
        return try {
            val instant = Instant.parse(utcString)
            val localDateTime = instant.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            localDateTime.format(formatter)
        }
        catch (e: Exception) {
            utcString
        }
    }
    fun formatTimestampToMonth(timestamp: Timestamp): String{
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return formatter.format(timestamp)
    }
}