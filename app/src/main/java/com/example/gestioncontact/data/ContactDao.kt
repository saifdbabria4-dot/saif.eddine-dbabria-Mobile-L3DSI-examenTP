package com.example.gestioncontact.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.gestioncontact.Contact

class ContactDao(context: Context) {
    private val dbHelper = ContactDbHelper.getInstance(context)

    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.COLUMN_NOM, contact.nom)
            put(ContactContract.COLUMN_PRENOM, contact.prenom)
            put(ContactContract.COLUMN_TELEPHONE, contact.telephone)
            put(ContactContract.COLUMN_EMAIL, contact.email)
            put(ContactContract.COLUMN_PHOTO_URI, contact.photoUri)
            contact.latitude?.let { put(ContactContract.COLUMN_LATITUDE, it) }
            contact.longitude?.let { put(ContactContract.COLUMN_LONGITUDE, it) }
        }
        return db.insert(ContactContract.TABLE_NAME, null, values)
    }

    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.COLUMN_NOM, contact.nom)
            put(ContactContract.COLUMN_PRENOM, contact.prenom)
            put(ContactContract.COLUMN_TELEPHONE, contact.telephone)
            put(ContactContract.COLUMN_EMAIL, contact.email)
            put(ContactContract.COLUMN_PHOTO_URI, contact.photoUri)
            put(ContactContract.COLUMN_LATITUDE, contact.latitude)
            put(ContactContract.COLUMN_LONGITUDE, contact.longitude)
        }
        return db.update(
            ContactContract.TABLE_NAME,
            values,
            "${ContactContract.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            ContactContract.TABLE_NAME,
            "${ContactContract.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun getAll(): List<Contact> {
        val list = mutableListOf<Contact>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${ContactContract.TABLE_NAME} ORDER BY ${ContactContract.COLUMN_NOM} COLLATE NOCASE, ${ContactContract.COLUMN_PRENOM} COLLATE NOCASE"
        db.rawQuery(query, null).use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_ID)
            val nomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_NOM)
            val prenomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PRENOM)
            val telCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_TELEPHONE)
            val emailCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_EMAIL)
            val photoCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PHOTO_URI)
            val latCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LATITUDE)
            val lonCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LONGITUDE)

            while (cursor.moveToNext()) {
                list.add(cursorToContact(cursor, idCol, nomCol, prenomCol, telCol, emailCol, photoCol, latCol, lonCol))
            }
        }
        return list
    }

    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${ContactContract.TABLE_NAME} WHERE ${ContactContract.COLUMN_ID} = ?"
        db.rawQuery(query, arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                val idCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_ID)
                val nomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_NOM)
                val prenomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PRENOM)
                val telCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_TELEPHONE)
                val emailCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_EMAIL)
                val photoCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PHOTO_URI)
                val latCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LATITUDE)
                val lonCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LONGITUDE)
                return cursorToContact(cursor, idCol, nomCol, prenomCol, telCol, emailCol, photoCol, latCol, lonCol)
            }
        }
        return null
    }

    fun search(text: String): List<Contact> {
        val list = mutableListOf<Contact>()
        val db = dbHelper.readableDatabase
        val pattern = "%$text%"
        val query = """
            SELECT * FROM ${ContactContract.TABLE_NAME} 
            WHERE ${ContactContract.COLUMN_NOM} LIKE ? OR ${ContactContract.COLUMN_PRENOM} LIKE ? OR ${ContactContract.COLUMN_TELEPHONE} LIKE ?
            ORDER BY ${ContactContract.COLUMN_NOM} COLLATE NOCASE, ${ContactContract.COLUMN_PRENOM} COLLATE NOCASE
        """.trimIndent()
        db.rawQuery(query, arrayOf(pattern, pattern, pattern)).use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_ID)
            val nomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_NOM)
            val prenomCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PRENOM)
            val telCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_TELEPHONE)
            val emailCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_EMAIL)
            val photoCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_PHOTO_URI)
            val latCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LATITUDE)
            val lonCol = cursor.getColumnIndexOrThrow(ContactContract.COLUMN_LONGITUDE)

            while (cursor.moveToNext()) {
                list.add(cursorToContact(cursor, idCol, nomCol, prenomCol, telCol, emailCol, photoCol, latCol, lonCol))
            }
        }
        return list
    }

    private fun cursorToContact(
        cursor: Cursor,
        idCol: Int,
        nomCol: Int,
        prenomCol: Int,
        telCol: Int,
        emailCol: Int,
        photoCol: Int,
        latCol: Int,
        lonCol: Int
    ): Contact {
        return Contact(
            id = cursor.getLong(idCol),
            nom = cursor.getString(nomCol),
            prenom = cursor.getString(prenomCol),
            telephone = cursor.getString(telCol),
            email = cursor.getString(emailCol),
            photoUri = cursor.getString(photoCol),
            latitude = if (cursor.isNull(latCol)) null else cursor.getDouble(latCol),
            longitude = if (cursor.isNull(lonCol)) null else cursor.getDouble(lonCol)
        )
    }
}
