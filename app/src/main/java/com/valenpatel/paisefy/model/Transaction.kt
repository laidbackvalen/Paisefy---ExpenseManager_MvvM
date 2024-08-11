package com.valenpatel.paisefy.model

import android.os.Parcel
import android.os.Parcelable
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
    val amount: Double,

    @ColumnInfo(name="expandBoolean")
    var isExpandable: Boolean, // Default value to false

    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB) val image: ByteArray?=null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(transType)
        parcel.writeString(category)
        parcel.writeString(account)
        parcel.writeString(note)
        parcel.writeLong(date.time)
        parcel.writeDouble(amount)
        parcel.writeByte(if (isExpandable) 1 else 0)
        parcel.writeByteArray(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}


