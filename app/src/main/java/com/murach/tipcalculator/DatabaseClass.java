package com.murach.tipcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberttat on 5/10/2017.
 */

public class DatabaseClass extends SQLiteOpenHelper {

    public static final String DB_NAME = "tips.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TIPS = "tips";
    private static final String COLUMN_ID = "_id";
    private static final long _id = 0;
    private static final String BILL_DATE = "_billDate";
    private static final String BILL_AMOUNT = "_billAmount";
    private static final String TIP_PERCENT = "_tipPercent";

    private static final String createTable =
            "CREATE TABLE " + TABLE_TIPS + " (" +
            COLUMN_ID + " INTERGER PRIMARY KEY AUTOINCREMENT, " +
            BILL_DATE + " INTEGER NOT NULL " +
            BILL_AMOUNT + " REAL " +
            TIP_PERCENT + "  REAL" + ");";

    public DatabaseClass(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creates db
        db.execSQL(createTable);

        addInitialTips();

     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drops if bill is in db, recreates db
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPS);
        onCreate(db);
    }

    public List<Tip> getTips ( ) {
        List<Tip> tipsArray = new ArrayList<Tip>( );
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TIPS + " WHERE 1", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Tip tip = cursorToTip(cursor);
            tipsArray.add(tip);
            cursor.moveToNext();
        }

        db.close();
        return tipsArray;
    }

    private Tip cursorToTip(Cursor cursor) {
        Tip tip = new Tip();
        tip.setId(cursor.getInt(0));
        tip.setDateMillis(cursor.getInt(1));
        tip.setBillAmount(cursor.getFloat(2));
        tip.setTipPercent(cursor.getFloat(3));
        return tip;
    }
/*
    public float getAverageTip ( ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE_TIPS, new String[] {"AVG(" + TIP_PERCENT + ")"}, null, null, null, null, null);
        c.moveToFirst();

        float average = c.getFloat(0);

        db.close();
        c.close();
        return average;
    }
    */

    public Tip getLastTip ( ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TIPS + " WHERE (" + COLUMN_ID + " = (SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_TIPS + "))", null);
        if (c.getCount() <= 0)
            return null;

        c.moveToFirst();

        Tip tip = cursorToTip(c);

        db.close();
        return tip;

    }

    public void addTip (Tip tip) {
        SQLiteDatabase db = getWritableDatabase();

        String insertStatement = "INSERT INTO " + TABLE_TIPS +
                "(" + BILL_AMOUNT + ", " + BILL_AMOUNT + ", " + TIP_PERCENT + ") VALUES (" +
                tip.getDateMillis() + ", " +
                tip.getBillAmount() + ", " +
                tip.getTipPercent() + ")";

        db.execSQL(insertStatement);
        db.close();
    }

    public void addInitialTips() {
        //get product info
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 0);
        values.put(BILL_DATE, 0);
        values.put(BILL_AMOUNT, 45.28);
        values.put(TIP_PERCENT, .15);

        values.put(COLUMN_ID, 1);
        values.put(BILL_DATE, 0);
        values.put(BILL_AMOUNT, 24.28);
        values.put(TIP_PERCENT, .15);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TIPS, null, values);
        //always close database to save memory consumption
        db.close();
    }
}
