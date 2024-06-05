package com.example.paisefy_moneymanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "transType")
    val transType: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "account")
    val account: String,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "amount")
    val amount: Double
)


