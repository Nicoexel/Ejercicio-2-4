package com.example.ejercicio_2_4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "signatures.db";

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "signatures";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FIRMA_DIGITAL = "firmaDigital";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_DESCRIPCION + " TEXT, " +
                    COLUMN_FIRMA_DIGITAL + " BLOB" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
