package com.example.collectomon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

public class CardDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "card_database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cards";
    public static final String ARTIST_NAME = "artist_name";
    public static final String CARD_ID = "card_id";
    public static final String COLUMN_IMAGE_SRC = "image_src";
    public static final String COLUMN_CARD_NAME = "card_name";
    public static final String COLUMN_SET_DETAILS = "set_details";
    public static final String COLUMN_CARD_DETAILS = "card_details";
    Context context;
    String dbPath;

    public CardDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ARTIST_NAME + " TEXT, " +
                CARD_ID + " TEXT, " +
                COLUMN_IMAGE_SRC + " TEXT, " +
                COLUMN_CARD_NAME + " TEXT, " +
                COLUMN_SET_DETAILS + " TEXT, " +
                COLUMN_CARD_DETAILS + " TEXT)";
        db.execSQL(createTableQuery);
        dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




    public void saveBackup() {
        String backupFileName = "CollectomonDatabase.db";
        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);

        try {
            SQLiteDatabase db = getWritableDatabase();

            File dbFile = new File(db.getPath());
            FileInputStream fis = new FileInputStream(dbFile);
            FileOutputStream fos = new FileOutputStream(backupFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            fis.close();

            Toast.makeText(context,"Database backup created successfully @downloads",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Failed to create backup",Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreBackup() {
        String backupFileName = "CollectomonDatabase.db";
        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);

        try {
            SQLiteDatabase db = getWritableDatabase();

            // Clear the existing table
            db.execSQL("DELETE FROM " + TABLE_NAME);

            FileInputStream fis = new FileInputStream(backupFile);
            FileOutputStream fos = new FileOutputStream(db.getPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            fis.close();

            Toast.makeText(context,"Database backup restored successfully from @downloads",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Failed to restore database backup, check @downloads folder",Toast.LENGTH_SHORT).show();
        }
    }




    public List<CardItem> getCardsByArtist(String artistName) {
        List<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ARTIST_NAME,
                CARD_ID,
                COLUMN_IMAGE_SRC,
                COLUMN_CARD_NAME,
                COLUMN_SET_DETAILS,
                COLUMN_CARD_DETAILS
        };

        String selection = ARTIST_NAME + " = ?";
        String[] selectionArgs = {artistName};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));

                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                cardList.add(cardItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return cardList;
    }


    public void addCards(List<CardItem> cards) {
        SQLiteDatabase db = getWritableDatabase();

        for (CardItem card : cards) {
            if (!isCardIdExists(db, card.getCardId())) {
                ContentValues values = new ContentValues();
                values.put(ARTIST_NAME, card.getArtistName());
                values.put(CARD_ID, card.getCardId());
                values.put(COLUMN_IMAGE_SRC, card.getImageUrl());
                values.put(COLUMN_CARD_NAME, card.getCardName());
                values.put(COLUMN_SET_DETAILS, card.getSetDetails());
                values.put(COLUMN_CARD_DETAILS, card.getCardDetails());

                db.insert(TABLE_NAME, null, values);
                Log.d("CardDatabase", "Added Card: Name" + card.getArtistName() + "ID:" + card.getCardId() + "IAMGE" + card.getImageUrl() + card.getCardName() + card.getCardDetails() + card.getSetDetails());
            } else {
                Log.d("CardDatabase", "Skipped Card: ID " + card.getCardId() + " already exists in the database.");
            }
        }

        db.close();
    }

    public void deleteCards(List<CardItem> cards) {
        SQLiteDatabase db = getWritableDatabase();

        for (CardItem card : cards) {
            String cardId = card.getCardId();

            if (isCardIdExists(db, cardId)) {
                db.delete(TABLE_NAME, CARD_ID + " = ?", new String[]{cardId});
                Log.d("CardDatabase", "Deleted Card: ID " + card.getCardId());
            } else {
                Log.d("CardDatabase", "Card: ID " + card.getCardId() + " does not exist in the database.");
            }
        }

        db.close();
    }


    private boolean isCardIdExists(SQLiteDatabase db, String cardId) {
        String[] columns = {CARD_ID};
        String selection = CARD_ID + " = ?";
        String[] selectionArgs = {cardId};
        String limit = "1";

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    public List<CardItem> getAllCards() {

        List<CardItem> list = new ArrayList<CardItem>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));

                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                list.add(cardItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }
    public boolean isCardExists(String cardId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {CARD_ID};
        String selection = CARD_ID + " = ?";
        String[] selectionArgs = {cardId};
        String limit = "1";

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        cursor.close();
        db.close();

        return exists;
    }
    /*
    public void addCard(CardItem cardItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CARD_ID, cardItem.getCardId());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

     */
    public void addCard(CardItem cardItem) {
        SQLiteDatabase db = getWritableDatabase();

        if (!isCardIdExists(db, cardItem.getCardId())) {
            ContentValues values = new ContentValues();
            values.put(ARTIST_NAME, cardItem.getArtistName()); // Add artist name to the ContentValues
            values.put(CARD_ID, cardItem.getCardId());
            values.put(COLUMN_IMAGE_SRC, cardItem.getImageUrl());
            values.put(COLUMN_CARD_NAME, cardItem.getCardName());
            values.put(COLUMN_SET_DETAILS, cardItem.getSetDetails());
            values.put(COLUMN_CARD_DETAILS, cardItem.getCardDetails());

            db.insert(TABLE_NAME, null, values);
            Log.d("CardDatabase", "Added Card: Name" + cardItem.getArtistName() + "ID:" + cardItem.getCardId() + "IAMGE" + cardItem.getImageUrl() + cardItem.getCardName() + cardItem.getCardDetails() + cardItem.getSetDetails());
        } else {
            Log.d("CardDatabase", "Skipped Card: ID " + cardItem.getCardId() + " already exists in the database.");
        }
        db.close();
    }

    public void deleteCard(CardItem cardItem) {
        SQLiteDatabase db = getWritableDatabase();

        String cardId = cardItem.getCardId();

        if (isCardIdExists(db, cardId)) {
            db.delete(TABLE_NAME, CARD_ID + " = ?", new String[]{cardId});
            Log.d("CardDatabase", "Deleted Card: ID " + cardItem.getCardId());
        } else {
            Log.d("CardDatabase", "Card: ID " + cardItem.getCardId() + " does not exist in the database.");
        }
        db.close();
    }





    public void updateCard(CardItem cardItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, cardItem.getArtistName());
        values.put(CARD_ID, cardItem.getCardId());
        values.put(COLUMN_IMAGE_SRC, cardItem.getImageSrc());
        values.put(COLUMN_CARD_NAME, cardItem.getCardName());
        values.put(COLUMN_SET_DETAILS, cardItem.getSetDetails());
        values.put(COLUMN_CARD_DETAILS, cardItem.getCardDetails());

        db.update(TABLE_NAME, values, CARD_ID + " = ?", new String[]{cardItem.getCardId()});
        db.close();
    }

}

