package com.card.businesscard.persistense.phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PhoneDatabaseHandler extends SQLiteOpenHelper implements IPhoneDatabaseHandler {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "businesscard";
    private static final String TABLE_NAME = "phones";
    private static final String PHONE_ID = "_id";
    private static final String CARD = "card_id";
    private static final String VALUE = "value";
    private final Context mContext;

    public PhoneDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GUESTS_TABLE = " CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME + " (\n"
                + this.PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD + " INTEGER       NOT NULL,\n"
                + this.VALUE + "   VARCHAR (250),\n"
                + "FOREIGN KEY (" + this.CARD + ") REFERENCES cards(_id) ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }

    @Override
    public List<String> getPhone(int _id) {
        List<String> contactList = new ArrayList<String>();
        String selectQuery = "SELECT  value FROM " + TABLE_NAME + " WHERE card_id =?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(_id)});

        if (cursor.moveToFirst()) {
            do {
                contactList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    @Override
    public void addPhone(String Phone, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newPhone = new ContentValues();
        newPhone.put(CARD, id);
        newPhone.put(VALUE, Phone);
        Long res = db.insert(TABLE_NAME, null, newPhone);
        db.close();

    }

    @Override
    public int updatePhone(String phone) {
        return 0;
    }

    @Override
    public void deletePhone(int card_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                CARD+" = ?",
                new String[] {String.valueOf(card_id)});
        db.close();
    }
}
