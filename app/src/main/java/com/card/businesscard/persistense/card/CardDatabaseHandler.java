package com.card.businesscard.persistense.card;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.card.businesscard.model.Card;
import java.util.ArrayList;
import java.util.List;


public class CardDatabaseHandler extends SQLiteOpenHelper implements ICardDatabaseHandler {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "businesscard";
    private static final String TABLE_NAME = "cards";
    private static final String CARD_NAME = "name";
    private static final String CARD_SURNAME = "surname";
    private static final String CARD_FATHERNAME = "fathername";
    private static final String CARD_ADDRESS = "address";
    private static final String CARD_IMAGE = "image";
    private static final String CARD_NOTES = "notes";
    private static final String ID = "_id";
    private static final String CARD = "card_id";
    private static final String VALUE = "value";
    private static final String TABLE_NAME_PHONES = "phones";
    private static final String TABLE_NAME_EMAILS = "emails";
    private static final String TABLE_NAME_SITES = "sites";
    private final Context mContext;

    public CardDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME + "(\n"
                + this.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD_NAME + " VARCHAR (250),\n"
                + this.CARD_SURNAME + "   VARCHAR (250),\n"
                + this.CARD_FATHERNAME + " VARCHAR (250),\n"
                + this.CARD_ADDRESS + "       VARCHAR (100),\n"
                + this.CARD_IMAGE + "   VARCHAR (100)   NOT NULL,\n"
                + this.CARD_NOTES + "  VARCHAR (250)\n"
                + ");";

        String SQL_CREATE_GUESTS_TABLE_PHONES = "CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME_PHONES + "(\n"
                + this.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD + " INTEGER NOT NULL,\n"
                + this.VALUE + "   VARCHAR (250),\n"
                + "FOREIGN KEY (" + this.CARD + ") REFERENCES cards(_id) ON DELETE CASCADE);";

        String SQL_CREATE_GUESTS_TABLE_EMAILS = "CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME_EMAILS + "(\n"
                + this.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD + " INTEGER NOT NULL,\n"
                + this.VALUE + "   VARCHAR (250),\n"
                + "FOREIGN KEY (" + this.CARD + ") REFERENCES cards(_id) ON DELETE CASCADE);";

        String SQL_CREATE_GUESTS_TABLE_SITES = "CREATE TABLE IF NOT EXISTS " + this.TABLE_NAME_SITES + "(\n"
                + this.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + this.CARD + " INTEGER NOT NULL,\n"
                + this.VALUE + "   VARCHAR (250),\n"
                + "FOREIGN KEY (" + this.CARD + ") REFERENCES cards(_id) ON DELETE CASCADE);";


        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE_PHONES);
        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE_EMAILS);
        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE_SITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 > i) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + this.TABLE_NAME+";");
            this.onCreate(sqLiteDatabase);
        }
    }


    @Override
    public Card getCard(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME, new String[]{ID,
                        CARD_NAME, CARD_SURNAME, CARD_FATHERNAME, CARD_ADDRESS, CARD_IMAGE, CARD_NOTES}, ID + "=?",
                new String[]{String.valueOf(_id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Card card = null;
        if (cursor != null) {
            card = new Card(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), null, cursor.getString(4), null, null, cursor.getString(5), cursor.getString(6));
        }

        return card;
    }

    @Override
    public Long addCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newCard = new ContentValues();
        newCard.put(CARD_NAME, card.get_name());
        newCard.put(CARD_SURNAME, card.get_surname());
        newCard.put(CARD_FATHERNAME, card.get_name());
        newCard.put(CARD_ADDRESS, card.get_address());
        newCard.put(CARD_FATHERNAME, card.get_fathername());
        newCard.put(CARD_IMAGE, card.get_image());
        newCard.put(CARD_NOTES, card.get_notes());
        Long res = db.insert(TABLE_NAME, null, newCard);
        db.close();
        return res;
    }

    @Override
    public List<Card> getAllCards() {
        List<Card> contactList = new ArrayList<Card>();
        String selectQuery = "SELECT  * FROM " + this.TABLE_NAME+";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Card contact = new Card();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.set_name(cursor.getString(1));
                contact.set_surname(cursor.getString(2));
                contact.set_fathername(cursor.getString(3));
                contact.set_address(cursor.getString(4));
                contact.set_image(cursor.getString(5));
                contact.set_notes(cursor.getString(6));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    @Override
    public int updateCard(Card card) {
        return 0;
    }

    @Override
    public void deleteCard(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                ID+" = ?",
                new String[] {String.valueOf(id)});
        db.close();
    }

    @Override
    public void deleteAllCards() {

    }

    @Override
    public void deleteSomeCards(List<Card> cards) {

    }
}
