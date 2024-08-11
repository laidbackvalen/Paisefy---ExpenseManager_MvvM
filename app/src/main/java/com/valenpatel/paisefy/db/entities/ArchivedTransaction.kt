package com.valenpatel.paisefy.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "archived_transactions")
class ArchivedTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

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
    val amount: Double,

    @ColumnInfo(name = "expandBoolean")
    var isExpandable: Boolean = false,

    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray? = null
)