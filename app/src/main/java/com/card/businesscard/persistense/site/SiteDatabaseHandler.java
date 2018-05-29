package com.card.businesscard.persistense.site;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SiteDatabaseHandler extends SQLiteOpenHelper implements ISiteDatabaseHandler {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "businesscard";
    private static final String TABLE_NAME = "sites";
    private static final String SITE_ID = "_id";
    private static final String CARD_ID = "card_id";
    private static final String VALUE = "value";
    private final Context mContext;

    public SiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GUESTS_TABLE = " CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME + " (\n"
                + this.SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD_ID + " INTEGER       NOT NULL,\n"
                + this.VALUE + "   VARCHAR (250),\n"
                + "FOREIGN KEY (" + this.CARD_ID + ") REFERENCES cards(_id) ON DELETE CASCADE);";



        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }

    @Override
    public List<String> getSite(int _id) {
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
    public void addSite(String site, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newSite = new ContentValues();
        newSite.put(CARD_ID, id);
        newSite.put(VALUE, site);
        Long res = db.insert(TABLE_NAME, null, newSite);
        db.close();

    }

    @Override
    public int updateSite(String site) {
        return 0;
    }

    @Override
    public void deleteSite(int card_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                CARD_ID+"  = ?",
                new String[] {String.valueOf(card_id)});
        db.close();
    }
}
