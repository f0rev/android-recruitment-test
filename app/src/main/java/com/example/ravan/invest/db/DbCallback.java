package com.example.ravan.invest.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

final class DbCallback extends SupportSQLiteOpenHelper.Callback {
  private static final int VERSION = 1;

  private static final String CREATE_ITEM = ""
      + "CREATE TABLE " + CurrenciesItem.TABLE + "("
      + CurrenciesItem.ID + " INTEGER NOT NULL PRIMARY KEY,"
      + CurrenciesItem.LIST_ID + " INTEGER NOT NULL REFERENCES " + CurrenciesItem.TABLE + "(" + CurrenciesItem.ID + "),"
      + CurrenciesItem.RESULT + " TEXT NOT NULL"
      + ")";
  private static final String CREATE_ITEM_LIST_ID_INDEX =
      "CREATE INDEX item_list_id ON " + CurrenciesItem.TABLE + " (" + CurrenciesItem.LIST_ID + ")";

  DbCallback() {
    super(VERSION);
  }

  @Override public void onCreate(SupportSQLiteDatabase db) {
    db.execSQL(CREATE_ITEM);
    db.execSQL(CREATE_ITEM_LIST_ID_INDEX);
  }

  @Override public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
