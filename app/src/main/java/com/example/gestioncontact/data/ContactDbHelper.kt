package com.example.gestioncontact.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gestioncontact.Contact

class ContactDbHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ContactDbHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE ${ContactContract.TABLE_NAME} (
                ${ContactContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContactContract.COLUMN_NOM} TEXT NOT NULL,
                ${ContactContract.COLUMN_PRENOM} TEXT NOT NULL,
                ${ContactContract.COLUMN_TELEPHONE} TEXT NOT NULL,
                ${ContactContract.COLUMN_EMAIL} TEXT,
                ${ContactContract.COLUMN_PHOTO_URI} TEXT,
                ${ContactContract.COLUMN_LATITUDE} REAL,
                ${ContactContract.COLUMN_LONGITUDE} REAL
            );
        """.trimIndent()
        db.execSQL(createTable)

        val demoContacts = listOf(
            Contact(0L, "Ben Ali", "Ahmed", "+216 20 123 456", "ahmed.benali@example.com", null, 36.8065, 10.1815),
            Contact(0L, "Trabelsi", "Sarra", "+216 98 765 432", "sarra.trabelsi@example.com", null, 36.8189, 10.1658),
            Contact(0L, "Gharbi", "Mohamed", "+216 55 111 222", "mohamed.gharbi@example.com", null, 36.7992, 10.1797),
            Contact(0L, "Chaabane", "Amira", "+216 99 888 777", "amira.chaabane@example.com", null, null, null),
            Contact(0L, "Bouazizi", "Karim", "+216 24 000 111", "karim.bouazizi@example.com", null, null, null)
        )

        for (c in demoContacts) {
            val values = ContentValues().apply {
                put(ContactContract.COLUMN_NOM, c.nom)
                put(ContactContract.COLUMN_PRENOM, c.prenom)
                put(ContactContract.COLUMN_TELEPHONE, c.telephone)
                put(ContactContract.COLUMN_EMAIL, c.email)
                put(ContactContract.COLUMN_PHOTO_URI, c.photoUri)
                c.latitude?.let { put(ContactContract.COLUMN_LATITUDE, it) }
                c.longitude?.let { put(ContactContract.COLUMN_LONGITUDE, it) }
            }
            db.insert(ContactContract.TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ContactContract.TABLE_NAME}")
        onCreate(db)
    }
}
