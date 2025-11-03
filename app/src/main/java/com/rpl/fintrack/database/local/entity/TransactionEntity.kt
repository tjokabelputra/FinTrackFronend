package com.rpl.fintrack.database.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "tid")
    val tid: Int = 0,

    @ColumnInfo(name = "uid")
    val uid: String = "",

    @ColumnInfo("type")
    val type: String = "",

    @ColumnInfo("name")
    val name: String = "",

    @ColumnInfo("Category")
    val category: String = "",

    @ColumnInfo("date")
    val date: String = "",

    @ColumnInfo("amount")
    val amount: Float = 0.0f,

    @ColumnInfo("description")
    val description: String = ""
)